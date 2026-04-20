package com.fiforum.routes

import com.fiforum.models.TeamsTable
import com.fiforum.models.UserData
import com.fiforum.models.Users
import com.fiforum.services.MatchingService
import com.fiforum.views.teamPage
import com.fiforum.views.waitingPage
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.userRoutes() {
    get("/myteam") {
        val emailAddr = call.parameters["email"] ?: return@get call.respondRedirect("/")
        
        val result = transaction {
            val userRow = Users.select { Users.email eq emailAddr }.singleOrNull()

            if (userRow == null) {
                null
            } else {
                val teamId = userRow[Users.teamId]
                if (teamId == null || !MatchingService.isLaunched) {
                    UserTeamResult.Waiting(userRow[Users.name], emailAddr)
                } else {
                    val teamRow = TeamsTable.select { TeamsTable.id eq teamId }.single()
                    val index = teamRow[TeamsTable.currentMissionIndex]
                    val color = teamRow[TeamsTable.teamColor]
                    val missionText = when(index) {
                        1 -> teamRow[TeamsTable.mission1]
                        2 -> teamRow[TeamsTable.mission2]
                        3 -> teamRow[TeamsTable.mission3]
                        else -> teamRow[TeamsTable.mission3]
                    } ?: "Find your team!"
                    
                    val members = Users.select { Users.teamId eq teamId }.map {
                        UserData(
                            it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], 
                            it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel],
                            it[Users.linkedinUrl], it[Users.xingUrl], it[Users.profilePicture],
                            it[Users.phonePrivate], it[Users.phoneWork], it[Users.address], it[Users.zipCode],
                            it[Users.joinedAt].toString(),
                            it[Users.hasDownloadedVCard]
                        )
                    }

                    // --- Badge Calculation ---
                    val badges = mutableListOf<String>()
                    val totalUsers = Users.selectAll().count()
                    val allUsersSorted = Users.selectAll().orderBy(Users.joinedAt to SortOrder.ASC).map { it[Users.email] }
                    val userRank = allUsersSorted.indexOf(emailAddr) + 1
                    
                    if (userRank <= totalUsers * 0.1 && totalUsers > 0) {
                        badges.add("alpha_10")
                    } else if (userRank <= totalUsers * 0.5 && totalUsers > 0) {
                        badges.add("beta_50")
                    }
                    
                    // Latecomer check (This is an approximation based on current state)
                    // If matched as latecomer or joined after isLaunched was true
                    if (MatchingService.isLaunched) {
                        // Normally we'd need to know if isLaunched was already true when they registered.
                        // For simplicity, if they aren't in a "original" match set (we don't track that explicitly yet)
                        // we can't be 100% sure without a 'registeredAtMatchLaunch' flag.
                        // Let's skip for now or use a heuristic.
                    }

                    // Team-based badges
                    if (members.size == 5) badges.add("full_house")
                    
                    val workstyles = members.map { it.workstyle }.distinct()
                    if (workstyles.size == 1 && members.size > 1) badges.add("hive_mind")
                    
                    val companies = members.map { it.company }.distinct()
                    if (companies.size == members.size && members.size > 1) badges.add("diversity_pro")

                    // Synergy Master (>4 shared interests)
                    val currentUser = members.find { it.email == emailAddr }
                    if (currentUser != null) {
                        var maxSynergy = 0
                        members.forEach { m ->
                            if (m.email != emailAddr) {
                                val s = MatchingService.calculatePairScore(currentUser, m)
                                if (s > maxSynergy) maxSynergy = s
                            }
                        }
                        // 4 shared interests = ~20-40 points depending on category. 
                        // Let's just check shared count directly
                        var sharedCount = 0
                        members.forEach { m ->
                            if (m.email != emailAddr) {
                                var count = 0
                                if (m.hobby == currentUser.hobby) count++
                                if (m.techInterest == currentUser.techInterest) count++
                                if (m.travel == currentUser.travel) count++
                                if (m.workstyle == currentUser.workstyle) count++
                                if (m.coffeeTalk == currentUser.coffeeTalk) count++
                                if (m.afterWork == currentUser.afterWork) count++
                                if (m.fuel == currentUser.fuel) count++
                                if (count > sharedCount) sharedCount = count
                            }
                        }
                        if (sharedCount > 4) badges.add("synergy_master")
                    }

                    if (userRow[Users.linkedinUrl]?.isNotBlank() == true && userRow[Users.name].isNotBlank()) {
                         badges.add("social_butterfly")
                    }
                    
                    if (userRow[Users.hasDownloadedVCard]) badges.add("network_node")

                    // Unicorn: Unique interest combo
                    val myCombo = "${userRow[Users.hobby]}|${userRow[Users.techInterest]}|${userRow[Users.travel]}"
                    val otherCombos = Users.select { Users.email neq emailAddr }.map { 
                        "${it[Users.hobby]}|${it[Users.techInterest]}|${it[Users.travel]}"
                    }
                    if (!otherCombos.contains(myCombo)) badges.add("unicorn")

                    UserTeamResult.InTeam(teamRow[TeamsTable.name], members, missionText, emailAddr, color, badges)
                }
            }
        }

        when (result) {
            is UserTeamResult.Waiting -> call.respondHtml { waitingPage(result.name, emptyList(), result.email) }
            is UserTeamResult.InTeam -> call.respondHtml { teamPage(result.teamName, result.members, result.mission, result.email, result.color, result.badges) }
            else -> call.respondRedirect("/")
        }
    }

    post("/api/vcard-downloaded") {
        val params = call.receiveParameters()
        val emailAddr = params["email"] ?: return@post call.respond(mapOf("status" to "error"))
        transaction {
            Users.update({ Users.email eq emailAddr }) {
                it[hasDownloadedVCard] = true
            }
        }
        call.respond(mapOf("status" to "ok"))
    }

    get("/api/waiting-users") {
        val users = transaction {
            Users.select { Users.teamId.isNull() }.map {
                UserData(
                    it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], 
                    it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel],
                    it[Users.linkedinUrl], it[Users.xingUrl], it[Users.profilePicture],
                    it[Users.phonePrivate], it[Users.phoneWork], it[Users.address], it[Users.zipCode],
                    it[Users.joinedAt].toString(),
                    it[Users.hasDownloadedVCard]
                )
            }
        }
        call.respond(users)
    }
}

sealed class UserTeamResult {
    data class Waiting(val name: String, val email: String) : UserTeamResult()
    data class InTeam(val teamName: String, val members: List<UserData>, val mission: String, val email: String, val color: String, val badges: List<String>) : UserTeamResult()
}

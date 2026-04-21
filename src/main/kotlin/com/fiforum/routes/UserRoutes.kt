package com.fiforum.routes

import com.fiforum.models.*
import com.fiforum.services.MatchingService
import com.fiforum.services.MatchingSocketService
import com.fiforum.views.teamPage
import com.fiforum.views.waitingPage
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.Duration

fun Route.userRoutes() {
    get("/myteam") {
        val emailAddr = call.parameters["email"] ?: return@get call.respondRedirect("/")
        
        val result = transaction {
            val userRow = Users.selectAll().where { Users.email eq emailAddr }.singleOrNull()

            if (userRow == null) {
                null
            } else {
                val teamId = userRow[Users.teamId]
                if (teamId == null || !MatchingService.isLaunched) {
                    UserTeamResult.Waiting(userRow[Users.name], emailAddr)
                } else {
                    val teamRow = TeamsTable.selectAll().where { TeamsTable.id eq teamId }.single()
                    val index = teamRow[TeamsTable.currentMissionIndex]
                    val color = teamRow[TeamsTable.teamColor]
                    
                    // Cooldown calculation
                    val lastClick = teamRow[TeamsTable.lastTeamFindClick]
                    val cooldownMs = if (lastClick != null) {
                        val diff = Duration.between(lastClick, LocalDateTime.now()).toMillis()
                        val remaining = (3 * 60 * 1000) - diff
                        if (remaining > 0) remaining else 0
                    } else 0
                    
                    val missionText = when(index) {
                        1 -> teamRow[TeamsTable.mission1]
                        2 -> teamRow[TeamsTable.mission2]
                        3 -> teamRow[TeamsTable.mission3]
                        else -> teamRow[TeamsTable.mission3]
                    } ?: "Find your team!"
                    
                    val members = Users.selectAll().where { Users.teamId eq teamId }.map { it.toUserData() }

                    // --- Joining Badge Calculation ---
                    val totalUsers = Users.selectAll().count()
                    val allUsersSorted = Users.selectAll().orderBy(Users.joinedAt to SortOrder.ASC).map { it[Users.email] }
                    
                    members.forEach { m ->
                        if (m.isLatecomer) {
                            m.joinBadge = "latecomer"
                        } else {
                            val userRank = allUsersSorted.indexOf(m.email) + 1
                            if (totalUsers > 0) {
                                m.joinBadge = when {
                                    userRank <= totalUsers * 0.1 -> "alpha_10"
                                    userRank <= totalUsers * 0.5 -> "beta_50"
                                    userRank > totalUsers * 0.9 -> "gamma_10"
                                    else -> "active_member"
                                }
                            }
                        }
                    }

                    // --- Current User Badges (for ID card) ---
                    val badges = mutableListOf<String>()
                    val currentUser = members.find { it.email == emailAddr }
                    currentUser?.joinBadge?.let { badges.add(it) }
                    
                    if (members.size == 5) badges.add("full_house")
                    val workstyles = members.map { it.workstyle }.distinct()
                    if (workstyles.size == 1 && members.size > 1) badges.add("hive_mind")
                    val companies = members.map { it.company }.distinct()
                    if (companies.size == members.size && members.size > 1) badges.add("diversity_pro")

                    val currentUser = members.find { it.email == emailAddr }
                    if (currentUser != null) {
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

                    val myCombo = "${userRow[Users.hobby]}|${userRow[Users.techInterest]}|${userRow[Users.travel]}"
                    val otherCombos = Users.selectAll().where { Users.email neq emailAddr }.map { 
                        "${it[Users.hobby]}|${it[Users.techInterest]}|${it[Users.travel]}"
                    }
                    if (!otherCombos.contains(myCombo)) badges.add("unicorn")

                    UserTeamResult.InTeam(teamRow[TeamsTable.name], members, missionText, emailAddr, color, badges, teamId, cooldownMs)
                }
            }
        }

        when (result) {
            is UserTeamResult.Waiting -> call.respondHtml { waitingPage(result.name, emptyList(), result.email) }
            is UserTeamResult.InTeam -> call.respondHtml { teamPage(result.teamName, result.members, result.mission, result.email, result.color, result.badges, result.teamId, result.cooldownMs) }
            else -> call.respondRedirect("/")
        }
    }

    post("/api/team-find") {
        val params = call.receiveParameters()
        val emailAddr = params["email"] ?: return@post call.respond(mapOf("status" to "error"))
        
        val teamId = transaction {
            val user = Users.selectAll().where { Users.email eq emailAddr }.singleOrNull()
            val tId = user?.get(Users.teamId)
            if (tId != null) {
                val team = TeamsTable.selectAll().where { TeamsTable.id eq tId }.single()
                val lastClick = team[TeamsTable.lastTeamFindClick]
                val canClick = lastClick == null || Duration.between(lastClick, LocalDateTime.now()).toMillis() > (3 * 60 * 1000)
                
                if (canClick) {
                    TeamsTable.update({ TeamsTable.id eq tId }) {
                        it[lastTeamFindClick] = LocalDateTime.now()
                    }
                    tId
                } else null
            } else null
        }

        if (teamId != null) {
            MatchingSocketService.broadcastTeamFind(teamId)
            call.respond(mapOf("status" to "ok"))
        } else {
            call.respond(mapOf("status" to "cooldown"))
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
            Users.selectAll().where { Users.teamId.isNull() }.map { it.toUserData() }
        }
        call.respond(users)
    }
}

sealed class UserTeamResult {
    data class Waiting(val name: String, val email: String) : UserTeamResult()
    data class InTeam(
        val teamName: String, 
        val members: List<UserData>, 
        val mission: String, 
        val email: String, 
        val color: String, 
        val badges: List<String>,
        val teamId: Int,
        val cooldownMs: Long
    ) : UserTeamResult()
}

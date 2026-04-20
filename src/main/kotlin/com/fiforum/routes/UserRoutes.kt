package com.fiforum.routes

import com.fiforum.models.TeamsTable
import com.fiforum.models.UserData
import com.fiforum.models.Users
import com.fiforum.services.MatchingService
import com.fiforum.views.teamPage
import com.fiforum.views.waitingPage
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
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
                    // Waiting
                    UserTeamResult.Waiting(userRow[Users.name], emailAddr)
                } else {
                    // In a team
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
                            it[Users.joinedAt].toString()
                        )
                    }
                    UserTeamResult.InTeam(teamRow[TeamsTable.name], members, missionText, emailAddr, color)
                }
            }
        }

        when (result) {
            is UserTeamResult.Waiting -> call.respondHtml { waitingPage(result.name, emptyList(), result.email) }
            is UserTeamResult.InTeam -> call.respondHtml { teamPage(result.teamName, result.members, result.mission, result.email, result.color) }
            else -> call.respondRedirect("/")
        }
    }

    get("/api/waiting-users") {
        val users = transaction {
            Users.select { Users.teamId.isNull() }.map {
                UserData(
                    it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], 
                    it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel],
                    it[Users.linkedinUrl], it[Users.xingUrl], it[Users.profilePicture],
                    it[Users.phonePrivate], it[Users.phoneWork], it[Users.address], it[Users.zipCode],
                    it[Users.joinedAt].toString()
                )
            }
        }
        call.respond(users)
    }
}

sealed class UserTeamResult {
    data class Waiting(val name: String, val email: String) : UserTeamResult()
    data class InTeam(val teamName: String, val members: List<UserData>, val mission: String, val email: String, val color: String) : UserTeamResult()
}

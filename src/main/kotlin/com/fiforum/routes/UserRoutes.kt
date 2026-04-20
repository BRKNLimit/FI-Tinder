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
        
        val userRow = transaction {
            Users.select { Users.email eq emailAddr }.singleOrNull()
        }

        if (userRow == null) {
            call.respondRedirect("/")
        } else {
            val teamId = userRow[Users.teamId]
            if (teamId == null || !MatchingService.isLaunched) {
                call.respondHtml { waitingPage(userRow[Users.name], emptyList(), emailAddr) }
            } else {
                val (teamName, teamMission, members) = transaction {
                    val teamRow = TeamsTable.select { TeamsTable.id eq teamId }.single()
                    val index = teamRow[TeamsTable.currentMissionIndex]
                    val missionText = when(index) {
                        1 -> teamRow[TeamsTable.mission1]
                        2 -> teamRow[TeamsTable.mission2]
                        3 -> teamRow[TeamsTable.mission3]
                        else -> teamRow[TeamsTable.mission3]
                    } ?: "Find your team!"
                    
                    val m = Users.select { Users.teamId eq teamId }.map {
                        UserData(
                            it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], 
                            it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel],
                            it[Users.linkedinUrl], it[Users.xingUrl], it[Users.profilePicture]
                        )
                    }
                    Triple(teamRow[TeamsTable.name], missionText, m)
                }
                call.respondHtml { teamPage(teamName, members, teamMission) }
            }
        }
    }

    get("/api/waiting-users") {
        val users = transaction {
            Users.select { Users.teamId.isNull() }.map {
                UserData(
                    it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], 
                    it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel],
                    it[Users.linkedinUrl], it[Users.xingUrl], it[Users.profilePicture]
                )
            }
        }
        call.respond(users)
    }
}

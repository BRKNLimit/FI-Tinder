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
            if (userRow == null) return@transaction null

            val teamId = userRow[Users.teamId]
            if (teamId == null || !MatchingService.isLaunched) {
                val waitingUsers = Users.select { Users.teamId.isNull() }.map {
                    UserData(
                        it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], 
                        it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel],
                        it[Users.linkedinUrl], it[Users.xingUrl], it[Users.profilePicture]
                    )
                }
                Triple(userRow[Users.name], waitingUsers, null)
            } else {
                val teamRow = TeamsTable.select { TeamsTable.id eq teamId }.single()
                val teamName = teamRow[TeamsTable.name]
                val teamMission = teamRow[TeamsTable.mission] ?: "Find your team and start the conversation!"
                
                val members = Users.select { Users.teamId eq teamId }.map {
                    UserData(
                        it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], 
                        it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel],
                        it[Users.linkedinUrl], it[Users.xingUrl], it[Users.profilePicture]
                    )
                }
                Quad(teamName, teamMission, emptyList<UserData>(), members)
            }
        }

        if (result == null) {
            call.respondRedirect("/")
        } else {
            if (result is Triple<*, *, *>) {
                val (name, waitingUsers, _) = result as Triple<String, List<UserData>, *>
                call.respondHtml { waitingPage(name, waitingUsers, emailAddr) }
            } else {
                val (teamName, mission, _, members) = result as Quad<String, String, *, List<UserData>>
                call.respondHtml { teamPage(teamName, members, mission) }
            }
        }
    }
}

data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

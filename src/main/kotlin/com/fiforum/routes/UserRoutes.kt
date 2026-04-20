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
                val waitingCount = Users.selectAll().count() - 1 // Exclude current user
                Triple(userRow[Users.name], waitingCount, null)
            } else {
                val teamName = TeamsTable.select { TeamsTable.id eq teamId }.single()[TeamsTable.name]
                val members = Users.select { Users.teamId eq teamId }.map {
                    UserData(it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel])
                }
                Triple(teamName, 0L, members)
            }
        }

        if (result == null) {
            call.respondRedirect("/")
        } else {
            val (nameOrTeam, waitingCount, members) = result
            if (members == null) {
                call.respondHtml { waitingPage(nameOrTeam as String, waitingCount as Long, emailAddr) }
            } else {
                call.respondHtml { teamPage(nameOrTeam as String, members as List<UserData>) }
            }
        }
    }
}

package com.fiforum.routes

import com.fiforum.models.*
import com.fiforum.services.AdminService
import com.fiforum.services.MatchingService
import com.fiforum.views.adminDashboard
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.adminRoutes() {
    get("/admin") {
        val data = transaction {
            val userCount = Users.selectAll().count()
            val teamCount = TeamsTable.selectAll().count()
            val isLaunched = MatchingService.isLaunched
            
            val allUsers = Users.selectAll().map { it.toUserData() }

            val teamsWithMembers = if (MatchingService.isLaunched) {
                TeamsTable.selectAll().map { teamRow ->
                    val teamId = teamRow[TeamsTable.id]
                    val members = Users.selectAll().where { Users.teamId eq teamId }.map { it.toUserData() }
                    teamRow[TeamsTable.name] to members
                }
            } else {
                emptyList()
            }
            
            AdminData(userCount, teamCount, isLaunched, allUsers, teamsWithMembers)
        }

        call.respondHtml { 
            adminDashboard(data.userCount, data.teamCount, data.isLaunched, data.teams, data.allUsers) 
        }
    }

    post("/admin/match") {
        MatchingService.runBatchMatching()
        com.fiforum.services.MatchingSocketService.broadcastMatchingFinished()
        call.respondRedirect("/admin")
    }

    post("/admin/reset") {
        AdminService.resetAll()
        call.respondRedirect("/admin")
    }
}

data class AdminData(
    val userCount: Long,
    val teamCount: Long,
    val isLaunched: Boolean,
    val allUsers: List<UserData>,
    val teams: List<Pair<String, List<UserData>>>
)

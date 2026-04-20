package com.fiforum.routes

import com.fiforum.models.TeamsTable
import com.fiforum.models.UserData
import com.fiforum.models.Users
import com.fiforum.services.AdminService
import com.fiforum.services.MatchingService
import com.fiforum.views.adminDashboard
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.adminRoutes() {
    get("/admin") {
        val data = transaction {
            val userCount = Users.selectAll().count()
            val teamCount = TeamsTable.selectAll().count()
            val isLaunched = MatchingService.isLaunched
            
            val allUsers = Users.selectAll().map {
                UserData(
                    it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], 
                    it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel],
                    it[Users.linkedinUrl], it[Users.xingUrl], it[Users.profilePicture],
                    it[Users.phonePrivate], it[Users.phoneWork], it[Users.address], it[Users.zipCode],
                    it[Users.joinedAt].toString()
                )
            }

            val teamsWithMembers = if (MatchingService.isLaunched) {
                TeamsTable.selectAll().map { teamRow ->
                    val teamId = teamRow[TeamsTable.id]
                    val members = Users.select { Users.teamId eq teamId }.map {
                        UserData(
                            it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], 
                            it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel],
                            it[Users.linkedinUrl], it[Users.xingUrl], it[Users.profilePicture],
                            it[Users.phonePrivate], it[Users.phoneWork], it[Users.address], it[Users.zipCode],
                            it[Users.joinedAt].toString()
                        )
                    }
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

    post("/admin/generate") {
        AdminService.generateMockData(20)
        call.respondRedirect("/admin")
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

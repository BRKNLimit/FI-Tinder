package com.fiforum.routes

import com.fiforum.models.TeamsTable
import com.fiforum.models.UserData
import com.fiforum.models.Users
import com.fiforum.services.AdminService
import com.fiforum.services.MatchingService
import com.fiforum.views.adminDashboard
import io.ktor.server.application.*
import io.ktor.server.html.*
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
            
            val allUsers = Users.selectAll().map {
                UserData(
                    it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], 
                    it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel],
                    it[Users.linkedinUrl], it[Users.xingUrl], it[Users.profilePicture]
                )
            }

            val teamsWithMembers = if (MatchingService.isLaunched) {
                TeamsTable.selectAll().map { teamRow ->
                    val teamId = teamRow[TeamsTable.id]
                    val members = Users.select { Users.teamId eq teamId }.map {
                        UserData(
                            it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], 
                            it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel],
                            it[Users.linkedinUrl], it[Users.xingUrl], it[Users.profilePicture]
                        )
                    }
                    teamRow[TeamsTable.name] to members
                }
            } else {
                emptyList()
            }
            
            val result = object {
                val uCount = userCount
                val tCount = teamCount
                val users = allUsers
                val teams = teamsWithMembers
            }
            result
        }
        
        call.respondHtml { 
            adminDashboard(data.uCount, data.tCount, MatchingService.isLaunched, data.teams, data.users) 
        }
    }

    post("/admin/generate") {
        AdminService.generateMockData(20)
        call.respondRedirect("/admin")
    }

    post("/admin/match") {
        MatchingService.runBatchMatching()
        call.respondRedirect("/admin")
    }

    post("/admin/reset") {
        AdminService.resetAll()
        call.respondRedirect("/admin")
    }
}

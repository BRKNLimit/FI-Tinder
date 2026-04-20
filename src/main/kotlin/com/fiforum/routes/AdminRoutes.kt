package com.fiforum.routes

import com.fiforum.models.TeamsTable
import com.fiforum.models.Users
import com.fiforum.services.AdminService
import com.fiforum.services.MatchingService
import com.fiforum.views.adminDashboard
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.adminRoutes() {
    get("/admin") {
        var userCount = 0L
        var teamCount = 0L
        transaction {
            userCount = Users.selectAll().count()
            teamCount = TeamsTable.selectAll().count()
        }
        call.respondHtml { adminDashboard(userCount, teamCount, MatchingService.isLaunched) }
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

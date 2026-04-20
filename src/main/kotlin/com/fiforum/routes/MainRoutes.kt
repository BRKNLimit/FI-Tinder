package com.fiforum.routes

import com.fiforum.models.UserData
import com.fiforum.models.Users
import com.fiforum.services.MatchingService
import com.fiforum.views.matchingFinishedGeneralPage
import com.fiforum.views.registrationPage
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.mainRoutes() {
    get("/") {
        call.respondHtml { registrationPage(MatchingService.isLaunched) }
    }

    post("/register") {
        val params = call.receiveParameters()
        val emailAddr = params["email"] ?: return@post call.respondRedirect("/")
        
        transaction {
            val exists = Users.select { Users.email eq emailAddr }.count() > 0
            if (!exists) {
                Users.insert {
                    it[email] = emailAddr
                    it[name] = params["name"] ?: "Anonymous"
                    it[company] = params["company"] ?: ""
                    it[hobby] = params["hobby"] ?: ""
                    it[techInterest] = params["techInterest"] ?: ""
                    it[travel] = params["travel"] ?: ""
                    it[workstyle] = params["workstyle"] ?: ""
                    it[coffeeTalk] = params["coffeeTalk"] ?: ""
                    it[afterWork] = params["afterWork"] ?: ""
                    it[popculture] = ""
                    it[fuel] = params["fuel"] ?: ""
                }
                
                if (MatchingService.isLaunched) {
                    val latecomer = UserData(
                        emailAddr, 
                        params["name"] ?: "Anonymous",
                        params["company"] ?: "",
                        params["hobby"] ?: "",
                        params["techInterest"] ?: "",
                        params["travel"] ?: "",
                        params["workstyle"] ?: "",
                        params["coffeeTalk"] ?: "",
                        params["afterWork"] ?: "",
                        "",
                        params["fuel"] ?: ""
                    )
                    MatchingService.assignLatecomer(latecomer)
                }
            }
        }
        call.respondRedirect("/myteam?email=$emailAddr")
    }

    post("/login") {
        val emailAddr = call.receiveParameters()["email"] ?: ""
        call.respondRedirect("/myteam?email=$emailAddr")
    }
}

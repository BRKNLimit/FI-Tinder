package com.fiforum.routes

import com.fiforum.models.UserData
import com.fiforum.models.Users
import com.fiforum.services.MatchingService
import com.fiforum.views.matchingFinishedGeneralPage
import com.fiforum.views.registrationPage
import com.fiforum.views.profilePage
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.mainRoutes() {
    get("/") {
        call.respondHtml { registrationPage(MatchingService.isLaunched) }
    }

    get("/profile") {
        val emailAddr = call.parameters["email"] ?: return@get call.respondRedirect("/")
        val user = transaction {
            Users.select { Users.email eq emailAddr }.singleOrNull()?.let {
                UserData(
                    it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], 
                    it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel],
                    it[Users.linkedinUrl], it[Users.xingUrl], it[Users.profilePicture]
                )
            }
        }
        if (user == null) call.respondRedirect("/")
        else call.respondHtml { profilePage(user) }
    }

    post("/profile/update") {
        val params = call.receiveParameters()
        val emailAddr = params["email"] ?: return@post call.respondRedirect("/")
        
        transaction {
            Users.update({ Users.email eq emailAddr }) {
                it[linkedinUrl] = params["linkedinUrl"]
                it[xingUrl] = params["xingUrl"]
                if (params["profilePicture"]?.isNotBlank() == true) {
                    it[profilePicture] = params["profilePicture"]
                }
            }
        }
        call.respondRedirect("/myteam?email=$emailAddr")
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
                    it[linkedinUrl] = params["linkedinUrl"]
                    it[xingUrl] = params["xingUrl"]
                    it[profilePicture] = params["profilePicture"]
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
                        params["fuel"] ?: "",
                        params["linkedinUrl"],
                        params["xingUrl"],
                        params["profilePicture"]
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

package com.fiforum.routes

import com.fiforum.models.*
import com.fiforum.services.MatchingService
import com.fiforum.views.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun Route.mainRoutes() {
    get("/") {
        call.respondHtml { loginRegisterPage() }
    }

    post("/auth") {
        val params = call.receiveParameters()
        val emailAddr = params["email"]?.lowercase()?.trim() ?: return@post call.respondRedirect("/")
        val password = params["password"] ?: ""

        // Special Admin Login
        if (emailAddr == "admin1234" && password == "Admin1234") {
            return@post call.respondRedirect("/admin")
        }

        val user = transaction {
            Users.selectAll().where { Users.email eq emailAddr }.singleOrNull()
        }

        if (user == null) {
            // New User: Create account with password hash
            transaction {
                Users.insert {
                    it[email] = emailAddr
                    it[passwordHash] = BCrypt.hashpw(password, BCrypt.gensalt())
                }
            }
            call.respondHtml { registrationPage(emailAddr, MatchingService.isLaunched) }
        } else {
            // Existing User: Verify password
            val hash = user[Users.passwordHash]
            if (BCrypt.checkpw(password, hash)) {
                // Login successful
                if (user[Users.name].isBlank()) {
                    // Authenticated but info missing
                    call.respondHtml { registrationPage(emailAddr, MatchingService.isLaunched) }
                } else {
                    // Fully registered
                    call.respondRedirect("/myteam?email=$emailAddr")
                }
            } else {
                call.respondHtml { loginRegisterPage("Ungültiges Passwort für diese Email.") }
            }
        }
    }

    get("/profile") {
        val emailAddr = call.parameters["email"] ?: return@get call.respondRedirect("/")
        val user = transaction {
            Users.selectAll().where { Users.email eq emailAddr }.singleOrNull()?.toUserData()
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
                it[phonePrivate] = params["phonePrivate"]
                it[phoneWork] = params["phoneWork"]
                it[address] = params["address"]
                it[zipCode] = params["zipCode"]
                it[allowVCardDownload] = params["allowVCardDownload"] == "on"
            }
        }
        call.respondRedirect("/myteam?email=$emailAddr")
    }

    post("/register") {
        val params = call.receiveParameters()
        val emailAddr = params["email"] ?: return@post call.respondRedirect("/")
        
        transaction {
            Users.update({ Users.email eq emailAddr }) {
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
                it[phonePrivate] = params["phonePrivate"]
                it[phoneWork] = params["phoneWork"]
                it[address] = params["address"]
                it[zipCode] = params["zipCode"]
                if (MatchingService.isLaunched) {
                    it[isLatecomer] = true
                }
            }
            
            if (MatchingService.isLaunched) {
                val latecomer = Users.selectAll().where { Users.email eq emailAddr }.single().toUserData()
                MatchingService.assignLatecomer(latecomer)
            }
        }
        call.respondRedirect("/myteam?email=$emailAddr")
    }

    post("/login") {
        val emailAddr = call.receiveParameters()["email"] ?: ""
        call.respondRedirect("/myteam?email=$emailAddr")
    }
}

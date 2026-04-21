package com.fiforum.routes

import com.fiforum.models.*
import com.fiforum.services.MatchingService
import com.fiforum.views.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import kotlinx.serialization.Serializable

fun Route.mainRoutes() {
    get("/") {
        call.respondHtml { loginRegisterPage() }
    }

    post("/register") {
        val params = call.receiveParameters()
        val emailAddr = params["email"]?.lowercase()?.trim() ?: return@post call.respondRedirect("/")
        val password = params["password"] ?: ""
        val name = params["name"] ?: "Anonymous"
        val company = params["company"] ?: ""

        val existing = transaction {
            Users.selectAll().where { Users.email eq emailAddr }.singleOrNull()
        }

        if (existing != null) {
            call.respondHtml { loginRegisterPage("Email existiert bereits.") }
        } else {
            transaction {
                Users.insert {
                    it[email] = emailAddr
                    it[passwordHash] = BCrypt.hashpw(password, BCrypt.gensalt())
                    it[Users.name] = name
                    it[Users.company] = company
                }
            }
            call.respondRedirect("/survey?email=$emailAddr")
        }
    }

    post("/login") {
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

        if (user != null && BCrypt.checkpw(password, user[Users.passwordHash])) {
            if (user[Users.q1] == null) {
                call.respondRedirect("/survey?email=$emailAddr")
            } else {
                call.respondRedirect("/myteam?email=$emailAddr")
            }
        } else {
            call.respondHtml { loginRegisterPage("Ungültige Email oder Passwort.") }
        }
    }

    get("/survey") {
        val email = call.parameters["email"] ?: return@get call.respondRedirect("/")
        call.respondHtml { surveyPage(email) }
    }

    @Serializable
    data class SurveySubmission(
        val email: String,
        val q1: String, val q2: String, val q3: String, val q4: String, val q5: String,
        val q6: String, val q7: String, val q8: String, val q9: String, val q10: String
    )

    post("/submit-survey") {
        val sub = call.receive<SurveySubmission>()
        transaction {
            Users.update({ Users.email eq sub.email }) {
                it[q1] = sub.q1; it[q2] = sub.q2; it[q3] = sub.q3; it[q4] = sub.q4; it[q5] = sub.q5
                it[q6] = sub.q6; it[q7] = sub.q7; it[q8] = sub.q8; it[q9] = sub.q9; it[q10] = sub.q10
            }
            
            if (MatchingService.isLaunched) {
                val latecomer = Users.selectAll().where { Users.email eq sub.email }.single().toUserData()
                MatchingService.assignLatecomer(latecomer)
            }
        }
        call.respond(mapOf("status" to "ok"))
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
}

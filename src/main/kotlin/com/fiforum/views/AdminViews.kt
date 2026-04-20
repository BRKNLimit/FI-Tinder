package com.fiforum.views

import kotlinx.html.*

fun HTML.adminDashboard(userCount: Long, teamCount: Long, isLaunched: Boolean) {
    layout("Admin X-Ray // Matchmaker") {
        div("container") {
            h1 { +"Admin X-Ray" }
            div("card") {
                p { +"Status: " ; span("accent-text") { if (isLaunched) +"MATCHING LAUNCHED" else +"WAITING FOR USERS" } }
                p { +"Registrierte User: " ; b { +"$userCount" } }
                p { +"Teams generiert: " ; b { +"$teamCount" } }
            }
            
            div("controls") {
                if (!isLaunched) {
                    form(action = "/admin/generate", method = FormMethod.post) {
                        button(type = ButtonType.submit) { +"Generate 20 Mock Users" }
                    }
                    form(action = "/admin/match", method = FormMethod.post) {
                        button(type = ButtonType.submit) { +"Start Matching Process" }
                    }
                }
                form(action = "/admin/reset", method = FormMethod.post) {
                    button(type = ButtonType.submit) { +"Reset Database" }
                }
            }
        }
    }
}

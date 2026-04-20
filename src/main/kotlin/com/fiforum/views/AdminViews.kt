package com.fiforum.views

import com.fiforum.models.UserData
import kotlinx.html.*

fun HTML.adminDashboard(
    userCount: Long, 
    teamCount: Long, 
    isLaunched: Boolean,
    teams: List<Pair<String, List<UserData>>> = emptyList()
) {
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

            if (isLaunched && teams.isNotEmpty()) {
                h2 { +"Team Analyse" }
                teams.forEach { (teamName, members) ->
                    div("card") {
                        h3("accent-text") { +teamName }
                        div {
                            members.forEach { m ->
                                div {
                                    style = "margin-bottom: 10px; border-bottom: 1px dotted #333; padding-bottom: 5px;"
                                    b { +m.name } ; span { +" (${m.company})" }
                                    div {
                                        small {
                                            +"Hobby: ${m.hobby} | Tech: ${m.techInterest} | Reise: ${m.travel} | Work: ${m.workstyle} | Coffee: ${m.coffeeTalk} | After: ${m.afterWork} | Fuel: ${m.fuel}"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

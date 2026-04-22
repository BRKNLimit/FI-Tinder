package com.fiforum.views

import com.fiforum.models.UserData
import kotlinx.html.*

fun HTML.adminDashboard(
    userCount: Long, 
    teamCount: Long, 
    isLaunched: Boolean,
    teams: List<Pair<String, List<UserData>>> = emptyList(),
    allUsers: List<UserData> = emptyList()
) {
    layout("Admin // Matchmaker") {
        div("container") {
            h1 { +"Admin Terminal" }
            
            div("grid-2") {
                div("card") {
                    h3 { +"SYSTEM STATUS" }
                    p { +"Users: $userCount" }
                    p { +"Teams: $teamCount" }
                    p { +"Status: "; b { if(isLaunched) +"LAUNCHED" else +"PRE-MATCHING" } }
                }
                div("card") {
                    h3 { +"CONTROLS" }
                    div {
                        style = "display: flex; flex-direction: column; gap: 10px;"
                        
                        if (!isLaunched) {
                            form(action = "/admin/match", method = FormMethod.post) {
                                button(type = ButtonType.submit) { 
                                    onClick = "document.getElementById('glitchOverlay').style.display = 'flex';"
                                    +"START MATCHING ENGINE" 
                                }
                            }
                            form(action = "/admin/generate", method = FormMethod.post) {
                                button(type = ButtonType.submit) { 
                                    style = "background: #111; border-color: #666;"
                                    +"GENERATE 20 MOCK USERS" 
                                }
                            }
                        } else {
                            p { +"Matching already completed." }
                        }

                        form(action = "/admin/reset", method = FormMethod.post) {
                            button(type = ButtonType.submit) { 
                                style = "background: #300; border-color: #f00; color: #f00; margin-top: 10px;"
                                +"RESET DATABASE (DESTRUCTIVE)" 
                            }
                        }
                    }
                }
            }

            h2 { +"User Database" }
            div("card") {
                style = "overflow-x: auto;"
                table {
                    style = "width: 100%; border-collapse: collapse; font-family: 'VT323'; font-size: 0.9rem;"
                    thead {
                        tr {
                            th { style = "text-align: left; padding: 10px; border-bottom: 1px solid #333;"; +"Name" }
                            th { style = "text-align: left; padding: 10px; border-bottom: 1px solid #333;"; +"Company" }
                            th { style = "text-align: left; padding: 10px; border-bottom: 1px solid #333;"; +"Answers (1-10)" }
                        }
                    }
                    tbody {
                        allUsers.forEach { user ->
                            tr {
                                td { style = "padding: 10px; border-bottom: 1px solid #222;"; +user.name }
                                td { style = "padding: 10px; border-bottom: 1px solid #222;"; +user.company }
                                td { 
                                    style = "padding: 10px; border-bottom: 1px solid #222; color: var(--accent);"
                                    val ans = listOfNotNull(user.q1, user.q2, user.q3, user.q4, user.q5, user.q6, user.q7, user.q8, user.q9, user.q10)
                                    if (ans.isEmpty()) +"(No survey yet)"
                                    else +ans.joinToString(", ")
                                }
                            }
                        }
                    }
                }
            }

            if (isLaunched) {
                h2 { +"Formed Teams" }
                div("grid-2") {
                    teams.forEach { (name, members) ->
                        div("card") {
                            h3("accent-text") { +name }
                            ul {
                                members.forEach { m ->
                                    li { +m.name; span { style="color: #666; font-size: 0.8rem; margin-left: 10px;"; +"(${m.company})" } }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

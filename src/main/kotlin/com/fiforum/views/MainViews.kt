package com.fiforum.views

import com.fiforum.models.UserData
import kotlinx.html.*

fun HTML.registrationPage(isLaunched: Boolean = false) {
    layout("Registration // Matchmaker") {
        div("container") {
            h1 { +"Matchmaker" }
            if (isLaunched) {
                div("card") {
                    style = "border-color: var(--accent); margin-bottom: 20px;"
                    h3("accent-text") { +"Spätanmelder-Modus" }
                    p { +"Das offizielle Matching ist beendet. Du wirst automatisch einem passenden Team zugewiesen." }
                }
            } else {
                p { +"Tritt der Community bei und finde dein perfektes Team." }
            }
            
            form(action = "/register", method = FormMethod.post) {
                div("input-group") {
                    label { +"Email (Login Identifier)" }
                    input(type = InputType.text) { name = "email"; required = true }
                }
                div("input-group") {
                    label { +"Vollständiger Name" }
                    input(type = InputType.text) { name = "name"; required = true }
                }
                div("input-group") {
                    label { +"Unternehmen / Organisation" }
                    select {
                        name = "company"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"Finanz Informatik" }
                        option { +"FI-TS" }
                        option { +"FI-SP" }
                        option { +"Star Finanz" }
                        option { +"inasys" }
                        option { +"FINMAS" }
                    }
                }
                div("input-group") {
                    label { +"Hobby" }
                    select {
                        name = "hobby"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"Fußball" }; option { +"Kochen" }; option { +"Gaming" }
                        option { +"Wandern" }; option { +"Lesen" }; option { +"Reisen" }
                        option { +"Fotografie" }; option { +"Musik" }; option { +"Yoga" }; option { +"Malen" }
                    }
                }
                div("input-group") {
                    label { +"Tech-Interesse" }
                    select {
                        name = "techInterest"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"Kotlin" }; option { +"AI" }; option { +"Cloud" }
                        option { +"Cyber Security" }; option { +"Blockchain" }; option { +"DevOps" }
                        option { +"Frontend" }; option { +"Backend" }; option { +"Mobile" }; option { +"Data Science" }
                    }
                }
                div("input-group") {
                    label { +"Dein Reiseziel" }
                    select { 
                        name = "travel"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"Asien" }
                        option { +"Nordamerika" }
                        option { +"Australien" }
                        option { +"Afrika" }
                        option { +"Skandinavien" }
                        option { +"Südamerika" }
                        option { +"Hauptsache Warm" }
                        option { +"Hauptsache Action" }
                        option { +"Süd Europa" }
                    }
                }
                div("input-group") {
                    label { +"Ich arbeite am liebsten..." }
                    select { 
                        name = "workstyle"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"... Remote" }
                        option { +"... im Office" }
                        option { +"... möglichst früh" }
                        option { +"... möglichst spät" }
                    }
                }
                div("input-group") {
                    label { +"Gespräche an der Kaffeemaschine" }
                    select {
                        name = "coffeeTalk"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"Tech-Gossip" }
                        option { +"Börse & Krypto" }
                        option { +"Sport-Ergebnisse" }
                        option { +"Filme & Serien" }
                        option { +"Haustiere & Alltag" }
                        option { +"Urlaubspläne" }
                        option { +"Gaming News" }
                        option { +"Lokale Events" }
                    }
                }
                div("input-group") {
                    label { +"Mein perfektes After Work..." }
                    select {
                        name = "afterWork"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"Feierabend Bier" }
                        option { +"Ab zum Sport" }
                        option { +"Ab auf die Couch" }
                        option { +"Side Hustle" }
                        option { +"Fancy Kochen" }
                    }
                }
                div("input-group") {
                    label { +"Mein Büro-Treibstoff..." }
                    select {
                        name = "fuel"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"Kaffee" }
                        option { +"Energy Drinks" }
                        option { +"Tee" }
                        option { +"Wasser (Stay Hydrated)" }
                        option { +"Snacks" }
                    }
                }
                
                button(type = ButtonType.submit) { +"Registrieren" }
            }
        }
    }
}

fun HTML.waitingPage(name: String, waitingCount: Long, email: String) {
    layout(
        title = "Waiting // Matchmaker",
        headContent = {
            meta {
                httpEquiv = "refresh"
                content = "3"
            }
        }
    ) {
        div("container") {
            h1 { +"Hallo, $name" }
            p { +"Du bist registriert. Das Matching hat noch nicht begonnen." }
            
            div("card") {
                p { +"Aktuell warten " ; span("accent-text") { +"$waitingCount" } ; +" andere Teilnehmer mit dir." }
            }

            div {
                span("spinner")
                span { +" Bitte warten..." }
            }
            p {
                small { +"Sobald der Admin den Prozess startet, erfährst du hier dein Team." }
            }
            a(href = "/myteam?email=$email") { button { +"Refreshen" } }
        }
    }
}

fun HTML.teamPage(teamName: String, members: List<UserData>) {
    // Identify shared interests (occurring more than once in the team)
    val sharedHobby = members.groupingBy { it.hobby }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys
    val sharedTech = members.groupingBy { it.techInterest }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys
    val sharedTravel = members.groupingBy { it.travel }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys
    val sharedWork = members.groupingBy { it.workstyle }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys
    val sharedCoffee = members.groupingBy { it.coffeeTalk }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys
    val sharedAfter = members.groupingBy { it.afterWork }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys
    val sharedFuel = members.groupingBy { it.fuel }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys

    layout("Your Team // Matchmaker") {
        div("container") {
            h1 { +"Dein Team" }
            h2("accent-text") { +teamName }
            
            div("team-list") {
                members.forEach { member ->
                    div("card") {
                        h3 { +member.name }
                        p { 
                            small { +"Firma: ${member.company}" } 
                        }
                        div {
                            if (member.hobby.isNotBlank()) {
                                span("badge ${if (sharedHobby.contains(member.hobby)) "badge-matched" else ""}") { +member.hobby }
                            }
                            if (member.techInterest.isNotBlank()) {
                                span("badge ${if (sharedTech.contains(member.techInterest)) "badge-matched" else ""}") { +member.techInterest }
                            }
                            if (member.travel.isNotBlank()) {
                                span("badge ${if (sharedTravel.contains(member.travel)) "badge-matched" else ""}") { +member.travel }
                            }
                            if (member.workstyle.isNotBlank()) {
                                span("badge ${if (sharedWork.contains(member.workstyle)) "badge-matched" else ""}") { +member.workstyle }
                            }
                            if (member.coffeeTalk.isNotBlank()) {
                                span("badge ${if (sharedCoffee.contains(member.coffeeTalk)) "badge-matched" else ""}") { +member.coffeeTalk }
                            }
                            if (member.afterWork.isNotBlank()) {
                                span("badge ${if (sharedAfter.contains(member.afterWork)) "badge-matched" else ""}") { +member.afterWork }
                            }
                            if (member.fuel.isNotBlank()) {
                                span("badge ${if (sharedFuel.contains(member.fuel)) "badge-matched" else ""}") { +member.fuel }
                            }
                        }
                    }
                }
            }
            
            p { +"Geht zu eurem Tisch und startet den Austausch!" }
        }
    }
}

fun HTML.matchingFinishedGeneralPage() {
    layout("Matching Finished // Matchmaker") {
        div("container") {
            h1 { +"Matching Beendet" }
            p { +"Der Registrierungsprozess ist abgeschlossen." }
            p { +"Bitte logge dich mit deiner Email ein, um dein Team zu sehen." }
            
            form(action = "/login", method = FormMethod.post) {
                div("input-group") {
                    label { +"Email" }
                    input(type = InputType.text) { name = "email"; required = true }
                }
                button(type = ButtonType.submit) { +"Login" }
            }
        }
    }
}

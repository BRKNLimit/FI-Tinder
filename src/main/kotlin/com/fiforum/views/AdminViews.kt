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
    layout(
        title = "Admin X-Ray // Matchmaker",
        headContent = {
            script {
                unsafe {
                    raw("""
                        const allUsers = ${allUsers.map { "{ name: '${it.name}', email: '${it.email}', company: '${it.company}', hobby: '${it.hobby}', tech: '${it.techInterest}', travel: '${it.travel}', work: '${it.workstyle}', coffee: '${it.coffeeTalk}', after: '${it.afterWork}', fuel: '${it.fuel}' }" }};
                        
                        function filterUsers() {
                            const search = document.getElementById('userSearch').value.toLowerCase();
                            const company = document.getElementById('companyFilter').value;
                            const hobby = document.getElementById('hobbyFilter').value;
                            const tech = document.getElementById('techFilter').value;
                            const travel = document.getElementById('travelFilter').value;
                            const fuel = document.getElementById('fuelFilter').value;

                            const filtered = allUsers.filter(u => {
                                const matchesSearch = u.name.toLowerCase().includes(search) || u.email.toLowerCase().includes(search);
                                const matchesCompany = company === "" || u.company === company;
                                const matchesHobby = hobby === "" || u.hobby === hobby;
                                const matchesTech = tech === "" || u.tech === tech;
                                const matchesTravel = travel === "" || u.travel === travel;
                                const matchesFuel = fuel === "" || u.fuel === fuel;
                                
                                return matchesSearch && matchesCompany && matchesHobby && matchesTech && matchesTravel && matchesFuel;
                            });

                            const container = document.getElementById('userListContainer');
                            const countSpan = document.getElementById('filteredCount');
                            countSpan.innerText = filtered.length;

                            container.innerHTML = filtered.map(u => `
                                <div class="card" style="margin-bottom: 5px; padding: 10px; font-size: 0.8rem;">
                                    <b class="accent-text">${"$"}{u.name}</b> (${"$"}{u.company})<br/>
                                    <small style="color: #888;">${"$"}{u.email}</small><br/>
                                    <small>${"$"}{u.hobby} | ${"$"}{u.tech} | ${"$"}{u.travel} | ${"$"}{u.fuel}</small>
                                </div>
                            `).join('');
                        }
                    """)
                }
            }
        }
    ) {
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

            h2 { +"User Filter" }
            div("card") {
                div("input-group") {
                    label { +"Suche (Name/Email)" }
                    input(type = InputType.text) { id = "userSearch"; onKeyUp = "filterUsers()"; placeholder = "Eingabe..." }
                }
                div("input-group") {
                    label { +"Unternehmen" }
                    select { 
                        id = "companyFilter"; onChange = "filterUsers()"
                        option { value = ""; +"Alle Unternehmen" }
                        allUsers.map { it.company }.distinct().filter { it.isNotBlank() }.sorted().forEach {
                            option { value = it; +it }
                        }
                    }
                }
                div("input-group") {
                    label { +"Hobby" }
                    select { 
                        id = "hobbyFilter"; onChange = "filterUsers()"
                        option { value = ""; +"Alle Hobbies" }
                        allUsers.map { it.hobby }.distinct().filter { it.isNotBlank() }.sorted().forEach {
                            option { value = it; +it }
                        }
                    }
                }
                div("input-group") {
                    label { +"Tech-Thema" }
                    select { 
                        id = "techFilter"; onChange = "filterUsers()"
                        option { value = ""; +"Alle Themen" }
                        allUsers.map { it.techInterest }.distinct().filter { it.isNotBlank() }.sorted().forEach {
                            option { value = it; +it }
                        }
                    }
                }
                div("input-group") {
                    label { +"Reiseziel" }
                    select { 
                        id = "travelFilter"; onChange = "filterUsers()"
                        option { value = ""; +"Alle Ziele" }
                        allUsers.map { it.travel }.distinct().filter { it.isNotBlank() }.sorted().forEach {
                            option { value = it; +it }
                        }
                    }
                }
                div("input-group") {
                    label { +"Büro-Treibstoff" }
                    select { 
                        id = "fuelFilter"; onChange = "filterUsers()"
                        option { value = ""; +"Alle Treibstoffe" }
                        allUsers.map { it.fuel }.distinct().filter { it.isNotBlank() }.sorted().forEach {
                            option { value = it; +it }
                        }
                    }
                }
                p { +"Gefundene Personen: " ; span("accent-text") { id = "filteredCount"; +"${allUsers.size}" } }
            }

            div {
                id = "userListContainer"
                style = "margin-top: 20px; max-height: 400px; overflow-y: auto;"
                allUsers.forEach { u ->
                    div("card") {
                        style = "margin-bottom: 5px; padding: 10px; font-size: 0.8rem;"
                        b("accent-text") { +u.name } ; +" (${u.company})"
                        br()
                        small { style = "color: #888;"; +u.email }
                        br()
                        small { +"${u.hobby} | ${u.techInterest} | ${u.travel}" }
                    }
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

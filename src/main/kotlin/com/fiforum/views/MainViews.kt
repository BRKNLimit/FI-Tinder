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

fun HTML.waitingPage(name: String, waitingUsers: List<UserData>, email: String) {
    layout(
        title = "Waiting // Matchmaker",
        headContent = {
            meta {
                httpEquiv = "refresh"
                content = "5" // Increased to 5s to allow for visual appreciation
            }
            script {
                unsafe {
                    raw("""
                        const waitingUsers = ${waitingUsers.map { "{ name: '${it.name}', company: '${it.company}', hobby: '${it.hobby}', tech: '${it.techInterest}', travel: '${it.travel}', work: '${it.workstyle}', coffee: '${it.coffeeTalk}', after: '${it.afterWork}', fuel: '${it.fuel}' }" }};
                    """)
                }
            }
        }
    ) {
        div("container") {
            h1 { +"Hallo, $name" }
            p { +"Du bist registriert. Das Matching hat noch nicht begonnen." }
            
            div("card") {
                style = "position: relative; height: 300px; overflow: hidden; background: #050505; border-style: solid; border-color: #222;"
                canvas {
                    id = "connectionCanvas"
                    style = "width: 100%; height: 100%;"
                }
                div {
                    style = "position: absolute; bottom: 10px; right: 10px; background: rgba(0,0,0,0.7); padding: 5px; font-size: 0.6rem; color: var(--text-secondary);"
                    +"Matching-Nodes: ${waitingUsers.size}"
                }
            }

            script {
                unsafe {
                    raw("""
                        const canvas = document.getElementById('connectionCanvas');
                        const ctx = canvas.getContext('2d');
                        let width, height;

                        function resize() {
                            width = canvas.offsetWidth;
                            height = canvas.height = canvas.offsetHeight;
                            canvas.width = width;
                        }
                        window.onresize = resize;
                        resize();

                        // Deterministic seed based on email
                        function hashCode(str) {
                            let hash = 0;
                            for (let i = 0; i < str.length; i++) {
                                hash = ((hash << 5) - hash) + str.charCodeAt(i);
                                hash |= 0;
                            }
                            return Math.abs(hash);
                        }

                        const particles = waitingUsers.map(u => {
                            const seed = hashCode(u.name + u.company);
                            return {
                                ...u,
                                x: (seed % 1000) / 1000 * width,
                                y: ((seed / 1000) % 1000) / 1000 * height,
                                vx: 0,
                                vy: 0,
                                radius: 3
                            };
                        });

                        function updatePositions() {
                            // Simple Force-Directed Layout logic
                            for (let i = 0; i < 50; i++) { // Run few iterations to stabilize
                                particles.forEach((p1, idx1) => {
                                    // Repulsion from other particles
                                    particles.forEach((p2, idx2) => {
                                        if (idx1 === idx2) return;
                                        const dx = p1.x - p2.x;
                                        const dy = p1.y - p2.y;
                                        const dist = Math.hypot(dx, dy) || 1;
                                        if (dist < 80) {
                                            const force = (80 - dist) / 80 * 2;
                                            p1.x += (dx / dist) * force;
                                            p1.y += (dy / dist) * force;
                                        }
                                    });
                                    // Repulsion from walls
                                    const margin = 20;
                                    if (p1.x < margin) p1.x += 2;
                                    if (p1.x > width - margin) p1.x -= 2;
                                    if (p1.y < margin) p1.y += 2;
                                    if (p1.y > height - margin) p1.y -= 2;
                                });
                            }
                        }
                        updatePositions();

                        function draw() {
                            ctx.clearRect(0, 0, width, height);
                            
                            // Draw static connections
                            for (let i = 0; i < particles.length; i++) {
                                for (let j = i + 1; j < particles.length; j++) {
                                    const p1 = particles[i];
                                    const p2 = particles[j];
                                    
                                    const isSameCompany = p1.company === p2.company && p1.company !== '';
                                    const hasInterestMatch = (p1.hobby === p2.hobby && p1.hobby !== '') ||
                                        (p1.tech === p2.tech && p1.tech !== '') ||
                                        (p1.travel === p2.travel && p1.travel !== '') ||
                                        (p1.work === p2.work && p1.work !== '') ||
                                        (p1.coffee === p2.coffee && p1.coffee !== '') ||
                                        (p1.after === p2.after && p1.after !== '') ||
                                        (p1.fuel === p2.fuel && p1.fuel !== '');

                                    if (isSameCompany || hasInterestMatch) {
                                        ctx.beginPath();
                                        ctx.moveTo(p1.x, p1.y);
                                        ctx.lineTo(p2.x, p2.y);
                                        // Permanent lines, no distance fade-out
                                        ctx.strokeStyle = isSameCompany ? 'rgba(255, 0, 0, 0.4)' : 'rgba(255, 255, 255, 0.1)';
                                        ctx.lineWidth = isSameCompany ? 1.5 : 0.5;
                                        ctx.stroke();
                                    }
                                }
                            }

                            // Draw stable particles
                            particles.forEach(p => {
                                ctx.beginPath();
                                ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
                                ctx.fillStyle = '#fff';
                                ctx.fill();
                            });
                        }
                        
                        // Draw once, or only on resize
                        draw();
                        // No requestAnimationFrame needed since it's now static
                    """)
                }
            }

            div {
                style = "margin-top: 20px;"
                span("spinner")
                span { +" Verbindung wird hergestellt..." }
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

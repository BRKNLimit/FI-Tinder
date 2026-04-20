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
                    label { +"Profilbild (Optional)" }
                    input(type = InputType.file) { 
                        id = "photoInput"; accept = "image/*"
                        onChange = """
                            const file = this.files[0];
                            const reader = new FileReader();
                            reader.onloadend = () => {
                                document.getElementById('profilePicture').value = reader.result;
                                document.getElementById('preview').src = reader.result;
                                document.getElementById('preview').style.display = 'block';
                            };
                            if (file) reader.readAsDataURL(file);
                        """.trimIndent()
                    }
                    input(type = InputType.hidden) { name = "profilePicture"; id = "profilePicture" }
                    img { id = "preview"; style = "max-width: 100px; display: none; margin-top: 10px; border: var(--border);" }
                }

                div("input-group") {
                    label { +"LinkedIn URL (Optional)" }
                    input(type = InputType.text) { name = "linkedinUrl"; placeholder = "https://linkedin.com/in/..." }
                }
                div("input-group") {
                    label { +"Xing URL (Optional)" }
                    input(type = InputType.text) { name = "xingUrl"; placeholder = "https://xing.com/profile/..." }
                }

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
                        option { +"Fußball" }; option { +"Wandern" }; option { +"Kochen" }; option { +"Gaming" }
                        option { +"Lesen" }; option { +"Reisen" }; option { +"Fotografie" }; option { +"Musik" }
                        option { +"Yoga" }; option { +"Malen" }; option { +"Gym / Fitness" }; option { +"Teamsport" }
                    }
                }
                div("input-group") {
                    label { +"Tech-Interesse" }
                    select {
                        name = "techInterest"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"Programmieren" }; option { +"AI" }; option { +"Cloud" }
                        option { +"Cyber Security" }; option { +"BlockChain" }; option { +"Devops" }
                        option { +"Data Science" }; option { +"FinTech" }; option { +"Agile/Scrum" }
                        option { +"Business Intelligence" }; option { +"UX/UI Design" }; option { +"Projektmanagement" }; option { +"E-Commerce" }
                    }
                }
                div("input-group") {
                    label { +"Dein Reiseziel" }
                    select { 
                        name = "travel"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"Asien" }
                        option { +"Nordamerika" }
                        option { +"Südamerika" }
                        option { +"Südeuropa" }
                        option { +"Skandinavien" }
                        option { +"Hauptsache warm" }
                        option { +"Hauptsache Action" }
                        option { +"Australien" }
                        option { +"Afrika" }
                        option { +"Städtetrip" }
                        option { +"Roadtrip" }
                        option { +"Balkonien" }
                    }
                }
                div("input-group") {
                    label { +"Ich arbeite am liebsten..." }
                    select { 
                        name = "workstyle"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"Remote" }
                        option { +"im Office" }
                        option { +"Hybrid" }
                        option { +"möglichst früh" }
                        option { +"möglichst spät" }
                    }
                }
                div("input-group") {
                    label { +"Gespräche an der Kaffeemaschine" }
                    select {
                        name = "coffeeTalk"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"Filme und Serien" }
                        option { +"Tech Gossip" }
                        option { +"Krypto und Finanzen" }
                        option { +"Sportergebnisse" }
                        option { +"Haustier und Alltag" }
                        option { +"Urlaubspläne" }
                        option { +"Gaming News" }
                        option { +"Lokale Events" }
                        option { +"Studium & Berufsschule" }
                    }
                }
                div("input-group") {
                    label { +"Mein perfektes After Work..." }
                    select {
                        name = "afterWork"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"Feierabendbier" }
                        option { +"ab zum Sport" }
                        option { +"ab auf die Couch" }
                        option { +"Side Hustle" }
                        option { +"Fancy kochen" }
                        option { +"Freunde treffen" }
                        option { +"Zocken" }
                    }
                }
                div("input-group") {
                    label { +"Mein Büro-Treibstoff..." }
                    select {
                        name = "fuel"; required = true
                        option { value = ""; disabled = true; selected = true; +"Bitte wählen..." }
                        option { +"Kaffee" }
                        option { +"Energy Drinks" }
                        option { +"Mate" }
                        option { +"Spezi / Cola" }
                        option { +"Tee" }
                        option { +"Wasser (stay hydrated)" }
                        option { +"Snacks" }
                    }
                }
                
                button(type = ButtonType.submit) { +"Registrieren" }
            }
        }
    }
}

fun HTML.profilePage(user: UserData) {
    layout("Profile // Matchmaker") {
        div("container") {
            h1 { +"Mein Profil" }
            p { +"Verwalte deine Networking-Links und dein Profilbild." }

            form(action = "/profile/update", method = FormMethod.post) {
                input(type = InputType.hidden) { name = "email"; value = user.email }
                
                div("input-group") {
                    label { +"Profilbild" }
                    if (user.profilePicture != null) {
                        img { 
                            src = user.profilePicture
                            style = "max-width: 150px; border: var(--border); margin-bottom: 10px; display: block;"
                            id = "currentProfilePic"
                        }
                    }
                    input(type = InputType.file) { 
                        id = "photoInput"; accept = "image/*"
                        onChange = """
                            const file = this.files[0];
                            const reader = new FileReader();
                            reader.onloadend = () => {
                                document.getElementById('profilePicture').value = reader.result;
                                const preview = document.getElementById('preview');
                                preview.src = reader.result;
                                preview.style.display = 'block';
                                if(document.getElementById('currentProfilePic')) document.getElementById('currentProfilePic').style.opacity = '0.3';
                            };
                            if (file) reader.readAsDataURL(file);
                        """.trimIndent()
                    }
                    input(type = InputType.hidden) { name = "profilePicture"; id = "profilePicture" }
                    img { id = "preview"; style = "max-width: 150px; display: none; margin-top: 10px; border: var(--border);" }
                    
                    button(type = ButtonType.button) {
                        style = "background: transparent; border: 1px solid var(--accent); color: var(--accent); font-size: 0.8rem; padding: 5px; margin-top: 10px; width: auto;"
                        onClick = "document.getElementById('profilePicture').value = ''; document.getElementById('preview').style.display = 'none'; if(document.getElementById('currentProfilePic')) document.getElementById('currentProfilePic').style.display = 'none';"
                        +"Bild entfernen"
                    }
                }

                div("input-group") {
                    label { +"LinkedIn URL" }
                    input(type = InputType.text) { 
                        name = "linkedinUrl"
                        value = user.linkedinUrl ?: ""
                        placeholder = "https://linkedin.com/in/..." 
                    }
                }
                div("input-group") {
                    label { +"Xing URL" }
                    input(type = InputType.text) { 
                        name = "xingUrl"
                        value = user.xingUrl ?: ""
                        placeholder = "https://xing.com/profile/..." 
                    }
                }

                button(type = ButtonType.submit) { +"Speichern & zurück zum Team" }
            }
            
            a(href = "/myteam?email=${user.email}") {
                p { style = "text-align: center; margin-top: 20px; text-decoration: underline; cursor: pointer;"; +"Abbrechen" }
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

                        const companyColors = {
                            'Star Finanz': '#ff0000',      // Red
                            'Finanz Informatik': '#ffff00', // Yellow
                            'inasys': '#00ff00',           // Green
                            'FI-TS': '#8000ff',            // Purple
                            'FI-SP': '#0080ff',            // Blue
                            'FINMAS': '#ff8000'            // Orange
                        };

                        const particles = waitingUsers.map(u => {
                            const seed = hashCode(u.name + u.company);
                            return {
                                ...u,
                                x: (seed % 1000) / 1000 * width,
                                y: ((seed / 1000) % 1000) / 1000 * height,
                                vx: 0,
                                vy: 0,
                                radius: 3,
                                color: companyColors[u.company] || '#ffffff'
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
                                        // Use company color for company lines, otherwise subtle white
                                        ctx.strokeStyle = isSameCompany ? p1.color : 'rgba(255, 255, 255, 0.1)';
                                        ctx.globalAlpha = isSameCompany ? 0.6 : 1.0;
                                        ctx.lineWidth = isSameCompany ? 1.5 : 0.5;
                                        ctx.stroke();
                                        ctx.globalAlpha = 1.0;
                                    }
                                }
                            }

                            // Draw stable particles
                            particles.forEach(p => {
                                ctx.beginPath();
                                ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
                                ctx.fillStyle = p.color;
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
                        div {
                            style = "display: flex; align-items: flex-start; gap: 20px;"
                            if (member.profilePicture != null) {
                                img { 
                                    src = member.profilePicture
                                    style = "width: 80px; height: 80px; object-fit: cover; border: var(--border);"
                                }
                            } else {
                                div {
                                    style = "width: 80px; height: 80px; border: var(--border); display: flex; align-items: center; justify-content: center; font-size: 2rem; color: var(--text-secondary);"
                                    +"?"
                                }
                            }
                            div {
                                style = "flex: 1;"
                                h3 { style = "margin-top: 0;"; +member.name }
                                p { 
                                    small { +"Firma: ${member.company}" } 
                                }
                                div {
                                    if (member.linkedinUrl?.isNotBlank() == true) {
                                        a(href = member.linkedinUrl, target = "_blank") {
                                            span("badge") { style = "color: #0077b5; border-color: #0077b5;"; +"LinkedIn" }
                                        }
                                    }
                                    if (member.xingUrl?.isNotBlank() == true) {
                                        a(href = member.xingUrl, target = "_blank") {
                                            span("badge") { style = "color: #026466; border-color: #026466;"; +"Xing" }
                                        }
                                    }
                                }
                            }
                        }
                        div {
                            style = "margin-top: 15px;"
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
            
            p { +"Vernetze dich jetzt und finde deine Team-Kollegen!" }
            a(href = "/profile?email=${members.find { it.email != null }?.email ?: ""}") { // This is a bit hacky, normally you'd have the current user's email
                 button { +"Mein Profil bearbeiten" }
            }
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

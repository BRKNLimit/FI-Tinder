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
            script {
                unsafe {
                    raw("""
                        const waitingUsers = ${waitingUsers.map { "{ name: '${it.name}', company: '${it.company}', hobby: '${it.hobby}', tech: '${it.techInterest}', travel: '${it.travel}', work: '${it.workstyle}', coffee: '${it.coffeeTalk}', after: '${it.afterWork}', fuel: '${it.fuel}' }" }};
                        const userEmail = '$email';
                        
                        // --- WebSocket Reveal Logic ---
                        function setupWebSocket() {
                            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
                            const ws = new WebSocket(protocol + '//' + window.location.host + '/matching-ws');
                            
                            ws.onmessage = (event) => {
                                if (event.data === 'MATCHING_FINISHED') {
                                    triggerGlitchReveal();
                                }
                            };
                            
                            ws.onclose = () => {
                                setTimeout(setupWebSocket, 2000); // Reconnect if dropped
                            };
                        }

                        function triggerGlitchReveal() {
                            const overlay = document.getElementById('glitchOverlay');
                            const status = document.getElementById('glitchStatus');
                            overlay.style.display = 'flex';
                            
                            let progress = 0;
                            const interval = setInterval(() => {
                                progress += Math.random() * 15;
                                if (progress >= 100) {
                                    progress = 100;
                                    clearInterval(interval);
                                    setTimeout(() => {
                                        window.location.href = '/myteam?email=' + userEmail;
                                    }, 500);
                                }
                                status.innerText = Math.floor(progress) + '%';
                            }, 100);
                        }

                        document.addEventListener('DOMContentLoaded', setupWebSocket);
                    """)
                }
            }
        }
    ) {
        div("container") {
            h1 { +"Hallo, $name" }
            p { +"Verbindung zum Netzwerk hergestellt. Warte auf Matching-Signal..." }
            
            div("card") {
                style = "position: relative; width: 350px; height: 350px; margin: 20px auto; overflow: hidden; background: #000; border: 1px solid #333;"
                canvas {
                    id = "connectionCanvas"
                    style = "width: 100%; height: 100%;"
                }
                div {
                    style = "position: absolute; bottom: 10px; right: 10px; background: rgba(0,0,0,0.7); padding: 5px; font-size: 0.6rem; color: var(--accent); font-family: 'VT323';"
                    +"NODES_ACTIVE: ${waitingUsers.size}"
                }
            }

            script {
                unsafe {
                    raw("""
                        const canvas = document.getElementById('connectionCanvas');
                        const ctx = canvas.getContext('2d');
                        let size = 350;
                        canvas.width = canvas.height = size;
                        
                        const companyColors = {
                            'Star Finanz': '#ff0000',
                            'Finanz Informatik': '#ffff00',
                            'inasys': '#00ff00',
                            'FI-TS': '#8000ff',
                            'FI-SP': '#0080ff',
                            'FINMAS': '#ff8000'
                        };

                        function hashCode(str) {
                            let hash = 0;
                            for (let i = 0; i < str.length; i++) {
                                hash = ((hash << 5) - hash) + str.charCodeAt(i);
                                hash |= 0;
                            }
                            return Math.abs(hash);
                        }

                        // Generate initial fixed positions
                        const particles = waitingUsers.map(u => {
                            const seed = hashCode(u.name + u.email);
                            return {
                                ...u,
                                // Position relative to center (0,0)
                                ox: (seed % 200) - 100, 
                                oy: ((seed / 200) % 200) - 100,
                                radius: 3,
                                color: companyColors[u.company] || '#ffffff'
                            };
                        });

                        // Run a quick force-separation on original coords to prevent overlaps
                        for(let i=0; i<100; i++) {
                            particles.forEach(p1 => {
                                particles.forEach(p2 => {
                                    if(p1 === p2) return;
                                    const dx = p1.ox - p2.ox;
                                    const dy = p1.oy - p2.oy;
                                    const dist = Math.hypot(dx, dy) || 1;
                                    if(dist < 30) {
                                        const force = (30 - dist) / 2;
                                        p1.ox += (dx/dist) * force;
                                        p1.oy += (dy/dist) * force;
                                    }
                                });
                            });
                        }

                        let angle = 0;
                        function animate() {
                            ctx.fillStyle = '#000';
                            ctx.fillRect(0, 0, size, size);
                            
                            const centerX = size / 2;
                            const centerY = size / 2;
                            angle += 0.002; // Slow rotation

                            // Calculate rotated positions
                            const rotated = particles.map(p => {
                                const cos = Math.cos(angle);
                                const sin = Math.sin(angle);
                                return {
                                    ...p,
                                    x: centerX + (p.ox * cos - p.oy * sin),
                                    y: centerY + (p.ox * sin + p.oy * cos)
                                };
                            });

                            // Draw Connections (Fixed logic, no jumping)
                            for (let i = 0; i < rotated.length; i++) {
                                for (let j = i + 1; j < rotated.length; j++) {
                                    const p1 = rotated[i];
                                    const p2 = rotated[j];
                                    
                                    const isSameCompany = p1.company === p2.company && p1.company !== '';
                                    const hasInterestMatch = (p1.hobby === p2.hobby) || (p1.tech === p2.tech);

                                    if (isSameCompany || hasInterestMatch) {
                                        ctx.beginPath();
                                        ctx.moveTo(p1.x, p1.y);
                                        ctx.lineTo(p2.x, p2.y);
                                        ctx.strokeStyle = isSameCompany ? p1.color : 'rgba(255, 255, 255, 0.1)';
                                        ctx.lineWidth = isSameCompany ? 1.5 : 0.5;
                                        ctx.stroke();
                                    }
                                }
                            }

                            // Draw Dots
                            rotated.forEach(p => {
                                ctx.beginPath();
                                ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
                                ctx.fillStyle = p.color;
                                ctx.fill();
                            });
                            
                            requestAnimationFrame(animate);
                        }
                        animate();
                    """)
                }
            }

            div {
                style = "margin-top: 20px;"
                span("spinner")
                span { +" Verbindung zum Server aktiv..." }
            }
        }
    }
}

fun HTML.teamPage(teamName: String, members: List<UserData>, mission: String) {
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

            div("card") {
                style = "border: 2px solid var(--accent); background: rgba(255, 0, 0, 0.05); margin-bottom: 25px;"
                h3("accent-text") { +"MISSION // ICEBREAKER" }
                p { 
                    style = "font-size: 1.2rem; margin: 10px 0;"
                    +mission 
                }
            }
            
            div("team-list") {
                members.forEach { member ->
                    div("card") {
                        div {
                            style = "display: flex; align-items: flex-start; gap: 20px;"
                            if (member.profilePicture?.isNotBlank() == true) {
                                img { 
                                    src = if (member.profilePicture!!.startsWith("data:")) member.profilePicture!! else "data:image/png;base64,${member.profilePicture}"
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
            
            div("card") {
                style = "border: 1px solid #333; margin-top: 20px; text-align: center;"
                h3 { +"DIGITAL_ID_CARD" }
                canvas {
                    id = "idCardCanvas"
                    width = "400"; height = "250"
                    style = "max-width: 100%; height: auto; border: 1px solid #fff; margin-bottom: 15px;"
                }
                button(type = ButtonType.button) {
                    onClick = "downloadIDCard()"
                    +"ID-CARD HERUNTERLADEN (.PNG)"
                }
            }

            script {
                unsafe {
                    raw("""
                        function generateIDCard() {
                            const canvas = document.getElementById('idCardCanvas');
                            const ctx = canvas.getContext('2d');
                            const user = ${members.find { it.email != null }?.let { "{ name: '${it.name}', company: '${it.company}', team: '$teamName', pic: '${it.profilePicture ?: ""}' }" } ?: "null"};
                            
                            if(!user) return;

                            // Background
                            ctx.fillStyle = '#000';
                            ctx.fillRect(0, 0, canvas.width, canvas.height);
                            
                            // Border
                            ctx.strokeStyle = '#fff';
                            ctx.setLineDash([5, 5]);
                            ctx.strokeRect(10, 10, canvas.width - 20, canvas.height - 20);
                            ctx.setLineDash([]);

                            // Profile Pic (if exists)
                            if(user.pic) {
                                const img = new Image();
                                img.onload = () => {
                                    ctx.drawImage(img, 20, 40, 80, 80);
                                    drawText();
                                };
                                img.src = user.pic;
                            } else {
                                ctx.strokeStyle = '#333';
                                ctx.strokeRect(20, 40, 80, 80);
                                ctx.fillStyle = '#333';
                                ctx.font = '40px VT323';
                                ctx.fillText('?', 50, 95);
                                drawText();
                            }

                            function drawText() {
                                ctx.fillStyle = '#fff';
                                ctx.font = '24px VT323';
                                ctx.fillText(user.name.toUpperCase(), 120, 60);
                                
                                ctx.font = '14px Inter';
                                ctx.fillStyle = '#a0a0a0';
                                ctx.fillText(user.company.toUpperCase(), 120, 85);
                                
                                ctx.fillStyle = '#ff0000';
                                ctx.font = '20px VT323';
                                ctx.fillText(user.team.toUpperCase(), 120, 120);

                                // Bottom "Nothing" Style elements
                                ctx.fillStyle = '#333';
                                ctx.font = '10px VT323';
                                ctx.fillText('MATCHMAKER // CONVENTION_EDITION_2026', 20, 230);
                                ctx.fillText('UID: ' + btoa(user.name).substring(0,8).toUpperCase(), 320, 230);
                            }
                        }

                        function downloadIDCard() {
                            const canvas = document.getElementById('idCardCanvas');
                            const link = document.createElement('a');
                            link.download = 'matchmaker-id-card.png';
                            link.href = canvas.toDataURL();
                            link.click();
                        }

                        document.addEventListener('DOMContentLoaded', generateIDCard);
                    """)
                }
            }

            a(href = "/profile?email=${members.find { it.email != null }?.email ?: ""}") {
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

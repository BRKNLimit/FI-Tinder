package com.fiforum.views

import com.fiforum.models.UserData
import kotlinx.html.*

fun HTML.loginRegisterPage(error: String? = null) {
    layout("Access // Matchmaker") {
        div("container") {
            h1 { +"Matchmaker Access" }
            p { +"Bitte gib deine Email und ein Passwort ein." }
            p { style = "font-size: 0.8rem; color: var(--text-secondary);"; +"Falls du noch nicht registriert bist, wird ein neuer Account erstellt." }

            if (error != null) {
                div("card") {
                    style = "border-color: var(--accent); color: var(--accent); margin-bottom: 20px;"
                    +error
                }
            }

            form(action = "/auth", method = FormMethod.post) {
                div("input-group") {
                    label { +"Email" }
                    input(type = InputType.text) { name = "email"; required = true }
                }
                div("input-group") {
                    label { +"Passwort" }
                    input(type = InputType.password) { name = "password"; required = true }
                }
                button(type = ButtonType.submit) { +"Weiter" }
            }
        }
    }
}

fun HTML.registrationPage(email: String, isLaunched: Boolean = false) {
    layout("Information // Matchmaker") {
        div("container") {
            h1 { +"Deine Infos" }
            p { +"Eingeloggt als: "; b { +email } }
            
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
                input(type = InputType.hidden) { name = "email"; value = email }
                
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

                h3 { +"Persönliche Daten (VCard)" }
                div("input-group") {
                    label { +"Telefon (Privat)" }
                    input(type = InputType.text) { name = "phonePrivate"; value = user.phonePrivate ?: "" }
                }
                div("input-group") {
                    label { +"Telefon (Arbeit)" }
                    input(type = InputType.text) { name = "phoneWork"; value = user.phoneWork ?: "" }
                }
                div("input-group") {
                    label { +"Adresse" }
                    input(type = InputType.text) { name = "address"; value = user.address ?: "" }
                }
                div("input-group") {
                    label { +"PLZ" }
                    input(type = InputType.text) { name = "zipCode"; value = user.zipCode ?: "" }
                }

                button(type = ButtonType.submit) { +"Speichern & zurück zum Team" }
            }
            
            a(href = "/myteam?email=${user.email}") {
                p { style = "text-align: center; margin-top: 20px; text-decoration: underline; cursor: pointer;"; +"Abbrechen" }
            }
        }
    }
}

fun HTML.waitingPage(name: String, initialWaitingUsers: List<UserData>, email: String) {
    layout(
        title = "Waiting // Matchmaker",
        headContent = {
            script {
                unsafe {
                    raw("""
                        const userEmail = '$email';
                        let particles = [];
                        let size = 350;
                        const centerX = size / 2;
                        const centerY = size / 2;
                        let angle = 0;

                        const companyColors = {
                            'Star Finanz': '#ff0000',
                            'Finanz Informatik': '#ffff00',
                            'inasys': '#00ff00',
                            'FI-TS': '#8000ff',
                            'FI-SP': '#0080ff',
                            'FINMAS': '#ff8000'
                        };
                        
                        const companyOffsets = {
                            'Star Finanz': { x: -60, y: -60 },
                            'Finanz Informatik': { x: 60, y: -60 },
                            'inasys': { x: 60, y: 60 },
                            'FI-TS': { x: -60, y: 60 },
                            'FI-SP': { x: 0, y: -80 },
                            'FINMAS': { x: 0, y: 80 }
                        };

                        function hashCode(str) {
                            let hash = 0;
                            for (let i = 0; i < str.length; i++) {
                                hash = ((hash << 5) - hash) + str.charCodeAt(i);
                                hash |= 0;
                            }
                            return Math.abs(hash);
                        }

                        async function refreshUsers() {
                            try {
                                const response = await fetch('/api/waiting-users');
                                const users = await response.json();
                                document.getElementById('nodeCount').innerText = users.length;
                                
                                users.forEach(u => {
                                    if (!particles.find(p => p.email === u.email)) {
                                        const seed = hashCode(u.name + u.email);
                                        const offset = companyOffsets[u.company] || { x: 0, y: 0 };
                                        
                                        particles.push({
                                            ...u,
                                            ox: offset.x + (seed % 60) - 30,
                                            oy: offset.y + ((seed / 60) % 60) - 30,
                                            radius: 3,
                                            color: companyColors[u.company] || '#ffffff'
                                        });
                                    }
                                });

                                for(let i=0; i<20; i++) {
                                    particles.forEach(p1 => {
                                        particles.forEach(p2 => {
                                            if(p1 === p2) return;
                                            const dx = p1.ox - p2.ox;
                                            const dy = p1.oy - p2.oy;
                                            const dist = Math.hypot(dx, dy) || 1;
                                            if(dist < 45) {
                                                const force = (45 - dist) / 4;
                                                p1.ox += (dx/dist) * force;
                                                p1.oy += (dy/dist) * force;
                                            }
                                        });
                                    });
                                }
                            } catch (e) { console.error("Refresh failed", e); }
                        }

                        function setupWebSocket() {
                            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
                            const ws = new WebSocket(protocol + '//' + window.location.host + '/matching-ws');
                            ws.onmessage = (event) => { if (event.data === 'MATCHING_FINISHED') triggerGlitchReveal(); };
                            ws.onclose = () => { setTimeout(setupWebSocket, 2000); };
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
                                    setTimeout(() => { window.location.href = '/myteam?email=' + userEmail; }, 500);
                                }
                                status.innerText = Math.floor(progress) + '%';
                            }, 100);
                        }

                        function animate() {
                            const canvas = document.getElementById('connectionCanvas');
                            if(!canvas) return;
                            const ctx = canvas.getContext('2d');
                            ctx.fillStyle = '#000';
                            ctx.fillRect(0, 0, size, size);
                            
                            angle += 0.0005;

                            const rotated = particles.map(p => {
                                const cos = Math.cos(angle);
                                const sin = Math.sin(angle);
                                return {
                                    ...p,
                                    x: centerX + (p.ox * cos - p.oy * sin),
                                    y: centerY + (p.ox * sin + p.oy * cos)
                                };
                            });

                            for (let i = 0; i < rotated.length; i++) {
                                for (let j = i + 1; j < rotated.length; j++) {
                                    const p1 = rotated[i];
                                    const p2 = rotated[j];
                                    const hasMatch = (p1.hobby === p2.hobby) || (p1.techInterest === p2.techInterest);

                                    if (hasMatch) {
                                        ctx.beginPath();
                                        ctx.moveTo(p1.x, p1.y);
                                        ctx.lineTo(p2.x, p2.y);
                                        ctx.strokeStyle = 'rgba(100, 100, 100, 0.15)';
                                        ctx.lineWidth = 0.5;
                                        ctx.stroke();
                                    }
                                }
                            }

                            rotated.forEach(p => {
                                ctx.beginPath();
                                ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
                                ctx.fillStyle = p.color;
                                ctx.fill();
                            });
                            
                            requestAnimationFrame(animate);
                        }

                        document.addEventListener('DOMContentLoaded', () => {
                            setupWebSocket();
                            refreshUsers();
                            setInterval(refreshUsers, 1000);
                            animate();
                        });
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
                    width = "350"; height = "350"
                    style = "width: 100%; height: 100%;"
                }
                div {
                    style = "position: absolute; bottom: 10px; right: 10px; background: rgba(0,0,0,0.7); padding: 5px; font-size: 0.6rem; color: var(--accent); font-family: 'VT323';"
                    +"NODES_ACTIVE: " ; span { id = "nodeCount" ; +"0" }
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

fun HTML.teamPage(teamName: String, members: List<UserData>, mission: String, currentUserEmail: String) {
    val sortedMembers = members.sortedByDescending { it.email.lowercase() == currentUserEmail.lowercase() }
    val currentUser = sortedMembers.firstOrNull { it.email.lowercase() == currentUserEmail.lowercase() }

    val sharedHobby = members.groupingBy { it.hobby }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys
    val sharedTech = members.groupingBy { it.techInterest }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys
    val sharedTravel = members.groupingBy { it.travel }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys
    val sharedWork = members.groupingBy { it.workstyle }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys
    val sharedCoffee = members.groupingBy { it.coffeeTalk }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys
    val sharedAfter = members.groupingBy { it.afterWork }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys
    val sharedFuel = members.groupingBy { it.fuel }.eachCount().filter { it.key.isNotBlank() && it.value > 1 }.keys

    layout(
        title = "Your Team // Matchmaker",
        headContent = {
            script { src = "https://cdn.jsdelivr.net/npm/qrcode-generator@1.4.4/qrcode.min.js" }
            script {
                unsafe {
                    raw("""
                        function setupTeamWebSocket() {
                            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
                            const ws = new WebSocket(protocol + '//' + window.location.host + '/matching-ws');
                            ws.onmessage = (event) => {
                                if (event.data === 'NEW_MISSION') {
                                    triggerMissionGlitch();
                                }
                            };
                            ws.onclose = () => setTimeout(setupTeamWebSocket, 2000);
                        }

                        function triggerMissionGlitch() {
                            const overlay = document.getElementById('glitchOverlay');
                            const status = document.getElementById('glitchStatus');
                            overlay.style.display = 'flex';
                            document.querySelector('.glitch-text').innerText = "NEW MISSION RECEIVED";
                            
                            let progress = 0;
                            const interval = setInterval(() => {
                                progress += 5;
                                if (progress >= 100) {
                                    clearInterval(interval);
                                    location.reload();
                                }
                                status.innerText = progress + '%';
                            }, 50);
                        }

                        function downloadVCard(u) {
                            const vcard = "BEGIN:VCARD\n" +
                                "VERSION:3.0\n" +
                                "FN:" + u.name + "\n" +
                                "ORG:" + u.company + "\n" +
                                "EMAIL:" + u.email + "\n" +
                                (u.phoneWork ? "TEL;TYPE=WORK,VOICE:" + u.phoneWork + "\n" : "") +
                                (u.phonePrivate ? "TEL;TYPE=HOME,VOICE:" + u.phonePrivate + "\n" : "") +
                                (u.address ? "ADR;TYPE=WORK:;;" + u.address + ";;;" + (u.zipCode || "") + ";\n" : "") +
                                (u.linkedin ? "URL:" + u.linkedin + "\n" : "") +
                                "END:VCARD";
                            
                            const blob = new Blob([vcard], { type: 'text/vcard' });
                            const url = window.URL.createObjectURL(blob);
                            const a = document.createElement('a');
                            a.href = url;
                            a.download = u.name.replace(' ', '_') + '.vcf';
                            a.click();
                        }

                        document.addEventListener('DOMContentLoaded', setupTeamWebSocket);
                    """)
                }
            }
        }
    ) {
        div("container") {
            h1 { +"Dein Team" }
            h2("accent-text") { +teamName }

            div("card") {
                id = "missionCard"
                style = "border: 2px solid var(--accent); background: rgba(255, 0, 0, 0.05); margin-bottom: 25px;"
                h3("accent-text") { +"MISSION // ICEBREAKER" }
                p { 
                    style = "font-size: 1.2rem; margin: 10px 0;"
                    +mission 
                }
            }
            
            div("team-list") {
                sortedMembers.forEach { member ->
                    div("card") {
                        if (member.email.lowercase() == currentUserEmail.lowercase()) {
                            style = "border: 1px solid var(--accent); box-shadow: inset 0 0 10px rgba(255,0,0,0.1);"
                        }
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
                                h3 { 
                                    style = "margin-top: 0;"
                                    +member.name
                                    if (member.email.lowercase() == currentUserEmail.lowercase()) {
                                        span { style = "color: var(--accent); font-size: 0.8rem; margin-left: 10px;"; +"(DU)" }
                                    }
                                }
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
                                    button(type = ButtonType.button) {
                                        style = "width: auto; padding: 2px 10px; font-size: 0.7rem; margin-top: 5px;"
                                        val uJson = """{ name: '${member.name}', company: '${member.company}', email: '${member.email}', phoneWork: '${member.phoneWork ?: ""}', phonePrivate: '${member.phonePrivate ?: ""}', address: '${member.address ?: ""}', zipCode: '${member.zipCode ?: ""}', linkedin: '${member.linkedinUrl ?: ""}' }"""
                                        onClick = "downloadVCard($uJson)"
                                        +"KONTAKT SPEICHERN (.VCF)"
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
                            const user = ${currentUser?.let { "{ name: '${it.name}', company: '${it.company}', team: '$teamName', pic: '${it.profilePicture ?: ""}', email: '${it.email}', phoneWork: '${it.phoneWork ?: ""}', phonePrivate: '${it.phonePrivate ?: ""}', address: '${it.address ?: ""}', zipCode: '${it.zipCode ?: ""}', linkedin: '${it.linkedinUrl ?: ""}' }" } ?: "null"};
                            
                            if(!user) return;

                            ctx.fillStyle = '#000';
                            ctx.fillRect(0, 0, canvas.width, canvas.height);
                            
                            ctx.strokeStyle = '#fff';
                            ctx.setLineDash([5, 5]);
                            ctx.strokeRect(10, 10, canvas.width - 20, canvas.height - 20);
                            ctx.setLineDash([]);

                            if(user.pic) {
                                const img = new Image();
                                img.onload = () => {
                                    ctx.drawImage(img, 20, 40, 80, 80);
                                    drawDetails();
                                };
                                img.src = user.pic;
                            } else {
                                ctx.strokeStyle = '#333';
                                ctx.strokeRect(20, 40, 80, 80);
                                ctx.fillStyle = '#333';
                                ctx.font = '40px VT323';
                                ctx.fillText('?', 50, 95);
                                drawDetails();
                            }

                            function drawDetails() {
                                ctx.fillStyle = '#fff';
                                ctx.font = '24px VT323';
                                ctx.fillText(user.name.toUpperCase(), 120, 60);
                                
                                ctx.font = '14px Inter';
                                ctx.fillStyle = '#a0a0a0';
                                ctx.fillText(user.company.toUpperCase(), 120, 85);
                                
                                ctx.fillStyle = '#ff0000';
                                ctx.font = '20px VT323';
                                ctx.fillText(user.team.toUpperCase(), 120, 120);

                                // Generate QR Code for VCard
                                const vcardData = "BEGIN:VCARD\nVERSION:3.0\nFN:" + user.name + "\nORG:" + user.company + "\nTEL:" + (user.phoneWork || user.phonePrivate || "") + "\nEMAIL:" + user.email + "\nEND:VCARD";
                                const qr = qrcode(0, 'M');
                                qr.addData(vcardData);
                                qr.make();
                                
                                // Draw QR Code on Canvas
                                const qrSize = 80;
                                const qrImg = new Image();
                                qrImg.onload = () => {
                                    ctx.fillStyle = '#fff';
                                    ctx.fillRect(300, 40, qrSize + 10, qrSize + 10); // White background for QR
                                    ctx.drawImage(qrImg, 305, 45, qrSize, qrSize);
                                };
                                qrImg.src = qr.createDataURL(4);

                                ctx.fillStyle = '#333';
                                ctx.font = '10px VT323';
                                ctx.fillText('MATCHMAKER // FI_FORUM_2026', 20, 230);
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

            a(href = "/profile?email=$currentUserEmail") {
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

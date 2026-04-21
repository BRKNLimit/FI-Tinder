package com.fiforum.views

import com.fiforum.models.UserData
import kotlinx.html.*

fun HTML.teamPage(teamName: String, members: List<UserData>, mission: String, currentUserEmail: String, teamColor: String, badges: List<String> = emptyList(), teamId: Int, cooldownMs: Long) {
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
            style {
                unsafe {
                    raw("""
                        :root {
                            --accent: $teamColor !important;
                        }
                        .badge-description {
                            font-family: 'VT323', monospace;
                            font-size: 0.8rem;
                            color: var(--accent);
                            margin-top: 10px;
                            min-height: 1.2rem;
                            text-transform: uppercase;
                        }
                        .grid-cell:hover {
                            background: rgba(255, 255, 255, 0.05);
                            color: #fff !important;
                        }
                        .team-find-overlay {
                            position: fixed;
                            top: 0; left: 0; width: 100%; height: 100%;
                            background: $teamColor;
                            z-index: 10000;
                            display: none;
                            flex-direction: column;
                            align-items: center;
                            justify-content: center;
                            color: #fff;
                            font-family: 'VT323', monospace;
                            text-align: center;
                        }
                        .team-find-overlay h1 { font-size: 4rem; color: #fff; }
                        .team-find-timer { font-size: 8rem; }
                        button:disabled {
                            background: #333 !important;
                            color: #666 !important;
                            cursor: not-allowed;
                        }
                    """)
                }
            }
            script {
                unsafe {
                    raw("""
                        const userEmail = '$currentUserEmail';
                        const badgeInfo = {
                            'ALPHA 10': 'Top 10% Early Bird - Einer der Ersten!',
                            'BETA 50': 'Top 50% Explorer - Früh dabei.',
                            'LATECOMER': 'Late joining reward.',
                            'SYNERGY MASTER': '>4 gemeinsame Interessen im Team.',
                            'UNICORN': 'Einzigartige Interessen-Kombi im Event.',
                            'SOCIAL BUTTERFLY': 'Profil vollständig mit LinkedIn & Bio.',
                            'DIVERSITY PRO': 'Team-Mix aus lauter versch. Firmen.',
                            'HIVE MIND': 'Gleicher Workstyle im ganzen Team.',
                            'FULL HOUSE': 'Perfekte Teamgröße von 5 Personen.',
                            'NETWORK NODE': 'VCard eines Kollegen gespeichert.'
                        };

                        function showBadgeDesc(name) {
                            const desc = badgeInfo[name] || "";
                            document.getElementById('badgeDescText').innerText = desc;
                        }

                        function clearBadgeDesc() {
                            document.getElementById('badgeDescText').innerText = "";
                        }

                        function setupTeamWebSocket() {
                            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
                            const ws = new WebSocket(protocol + '//' + window.location.host + '/matching-ws?teamId=$teamId');
                            ws.onmessage = (event) => {
                                if (event.data === 'NEW_MISSION') {
                                    triggerMissionGlitch();
                                } else if (event.data === 'TEAM_FINDEN') {
                                    showTeamFindOverlay();
                                }
                            };
                            ws.onclose = () => setTimeout(setupTeamWebSocket, 2000);
                        }

                        function showTeamFindOverlay() {
                            const overlay = document.getElementById('teamFindOverlay');
                            const timer = document.getElementById('teamFindTimer');
                            overlay.style.display = 'flex';
                            
                            let seconds = 30;
                            const interval = setInterval(() => {
                                seconds--;
                                timer.innerText = seconds;
                                if (seconds <= 0) {
                                    clearInterval(interval);
                                    overlay.style.display = 'none';
                                }
                            }, 1000);
                        }

                        async function teamFinden() {
                            const btn = document.getElementById('teamFindButton');
                            btn.disabled = true;
                            try {
                                const response = await fetch('/api/team-find', {
                                    method: 'POST',
                                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                                    body: 'email=' + encodeURIComponent(userEmail)
                                });
                                const result = await response.json();
                                if (result.status === 'ok') {
                                    startCooldown(180000); // 3 mins
                                } else if (result.status === 'cooldown') {
                                    alert("Cooldown aktiv!");
                                }
                            } catch(e) { btn.disabled = false; }
                        }

                        function startCooldown(ms) {
                            const btn = document.getElementById('teamFindButton');
                            btn.disabled = true;
                            let remaining = ms;
                            const interval = setInterval(() => {
                                remaining -= 1000;
                                if (remaining <= 0) {
                                    clearInterval(interval);
                                    btn.disabled = false;
                                    btn.innerText = "TEAM FINDEN";
                                } else {
                                    const mins = Math.floor(remaining / 60000);
                                    const secs = Math.floor((remaining % 60000) / 1000);
                                    btn.innerText = "COOLDOWN (" + mins + ":" + (secs < 10 ? "0" : "") + secs + ")";
                                }
                            }, 1000);
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

                        async function downloadVCard(u) {
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
                            
                            try {
                                await fetch('/api/vcard-downloaded', {
                                    method: 'POST',
                                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                                    body: 'email=' + encodeURIComponent(userEmail)
                                });
                            } catch(e) {}

                            const blob = new Blob([vcard], { type: 'text/vcard' });
                            const url = window.URL.createObjectURL(blob);
                            const a = document.createElement('a');
                            a.href = url;
                            a.download = u.name.replace(' ', '_') + '.vcf';
                            a.click();
                        }

                        document.addEventListener('DOMContentLoaded', () => {
                            setupTeamWebSocket();
                            const initialCooldown = $cooldownMs;
                            if (initialCooldown > 0) startCooldown(initialCooldown);
                        });
                    """)
                }
            }
        }
    ) {
        div("team-find-overlay") {
            id = "teamFindOverlay"
            h1 { +"DEIN TEAM SUCHT DICH!" }
            div("team-find-timer") { id = "teamFindTimer"; +"30" }
            div {
                style = "margin-top: 40px; display: flex; flex-direction: column; gap: 10px;"
                members.forEach { member ->
                    div { 
                        style = "font-size: 2rem; text-transform: uppercase;"
                        +member.name 
                    }
                }
            }
        }

        div("container") {
            button(type = ButtonType.button) {
                id = "teamFindButton"
                style = "margin-bottom: 20px; font-size: 1.5rem;"
                onClick = "teamFinden()"
                +"TEAM FINDEN"
            }

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
                                    src = if (member.profilePicture.startsWith("data:")) member.profilePicture else "data:image/png;base64,${member.profilePicture}"
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
                                    member.joinBadge?.let { jb ->
                                        span("badge") { 
                                            style = "font-size: 0.6rem; margin-left: 10px; vertical-align: middle; border-color: var(--accent); color: var(--accent);"
                                            +jb.replace('_', ' ').uppercase() 
                                        }
                                    }
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
                                    if (member.allowVCardDownload) {
                                        button(type = ButtonType.button) {
                                            style = "width: auto; padding: 2px 10px; font-size: 0.7rem; margin-top: 5px;"
                                            val uJson = """{ name: '${member.name}', company: '${member.company}', email: '${member.email}', phoneWork: '${member.phoneWork ?: ""}', phonePrivate: '${member.phonePrivate ?: ""}', address: '${member.address ?: ""}', zipCode: '${member.zipCode ?: ""}', linkedin: '${member.linkedinUrl ?: ""}' }"""
                                            onClick = "downloadVCard($uJson)"
                                            +"KONTAKT SPEICHERN (.VCF)"
                                        }
                                    } else {
                                        p { style = "font-size: 0.6rem; color: var(--text-secondary); margin-top: 5px;"; +"KONTAKT-DOWNLOAD DEAKTIVIERT" }
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
                style = "border: 1px solid #333; margin-top: 20px; text-align: center; border-style: none;"
                h3 { +"DIGITAL_ID_CARD (CLICK TO FLIP)" }
                
                div("id-card-perspective") {
                    id = "idCardPerspective"
                    onClick = "this.classList.toggle('flipped')"
                    div("id-card-inner") {
                        div("id-card-front") {
                            canvas {
                                id = "idCardCanvas"
                                width = "400"; height = "250"
                                style = "width: 100%; height: 100%;"
                            }
                        }
                        div("id-card-back") {
                            div("grid-5x2") {
                                badges.take(10).forEach { b ->
                                    val bName = b.replace('_', ' ').uppercase()
                                    div("grid-cell") { 
                                        style = "color: var(--accent); font-weight: bold; font-size: 0.6rem;"
                                        onMouseOver = "showBadgeDesc('$bName')"
                                        onMouseOut = "clearBadgeDesc()"
                                        +bName
                                    }
                                }
                                repeat(10 - badges.size) {
                                    div("grid-cell") { +"." }
                                }
                            }
                            div("badge-description") {
                                id = "badgeDescText"
                                +"Halt die Maus über ein Badge!"
                            }
                        }
                    }
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
                                
                                ctx.font = '16px VT323';
                                ctx.fillStyle = '#a0a0a0';
                                ctx.fillText(user.company.toUpperCase(), 120, 85);
                                
                                ctx.fillStyle = '$teamColor';
                                ctx.font = '22px VT323';
                                ctx.fillText(user.team.toUpperCase(), 120, 120);

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

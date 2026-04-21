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

                div("input-group") {
                    label { 
                        input(type = InputType.checkBox) { name = "allowVCardDownload"; checked = true; style = "width: auto; margin-right: 10px;" }
                        +"Teamkollegen dürfen meine Kontaktdaten (VCard) herunterladen"
                    }
                }
                
                button(type = ButtonType.submit) { +"Registrieren" }
            }
        }
    }
}

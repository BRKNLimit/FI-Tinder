package com.fiforum.views

import com.fiforum.models.UserData
import kotlinx.html.*

fun HTML.loginRegisterPage(error: String? = null) {
    layout("Access // Matchmaker") {
        div("container") {
            h1 { +"Matchmaker Access" }
            
            div("tabs") {
                style = "display: flex; gap: 20px; margin-bottom: 30px; border-bottom: 1px solid #333;"
                div("tab active") { 
                    id = "regTab"; style = "padding: 10px; cursor: pointer; border-bottom: 2px solid #fff;"
                    onClick = "showSection('register')"
                    +"REGISTER" 
                }
                div("tab") { 
                    id = "logTab"; style = "padding: 10px; cursor: pointer;"
                    onClick = "showSection('login')"
                    +"LOGIN" 
                }
            }

            if (error != null) {
                div("card") {
                    style = "border-color: var(--accent); color: var(--accent); margin-bottom: 20px;"
                    +error
                }
            }

            div {
                id = "registerSection"
                form(action = "/register", method = FormMethod.post) {
                    div("input-group") {
                        label { +"Email" }
                        input(type = InputType.text) { name = "email"; required = true }
                    }
                    div("input-group") {
                        label { +"Passwort" }
                        input(type = InputType.password) { name = "password"; required = true }
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
                    button(type = ButtonType.submit) { +"Registrieren" }
                }
            }

            div {
                id = "loginSection"; style = "display: none;"
                form(action = "/login", method = FormMethod.post) {
                    div("input-group") {
                        label { +"Email" }
                        input(type = InputType.text) { name = "email"; required = true }
                    }
                    div("input-group") {
                        label { +"Passwort" }
                        input(type = InputType.password) { name = "password"; required = true }
                    }
                    button(type = ButtonType.submit) { +"Login" }
                }
            }

            script {
                unsafe {
                    raw("""
                        function showSection(type) {
                            const regSec = document.getElementById('registerSection');
                            const logSec = document.getElementById('loginSection');
                            const regTab = document.getElementById('regTab');
                            const logTab = document.getElementById('logTab');
                            
                            if (type === 'register') {
                                regSec.style.display = 'block';
                                logSec.style.display = 'none';
                                regTab.style.borderBottom = '2px solid #fff';
                                logTab.style.borderBottom = 'none';
                            } else {
                                regSec.style.display = 'none';
                                logSec.style.display = 'block';
                                regTab.style.borderBottom = 'none';
                                logTab.style.borderBottom = '2px solid #fff';
                            }
                        }
                    """)
                }
            }
        }
    }
}

fun HTML.registrationPage(email: String, isLaunched: Boolean = false) {
    // This is now replaced by the direct register on landing, 
    // but we keep the structure for compatibility or specialized profile info
    layout("Information // Matchmaker") {
        div("container") {
            h1 { +"Profil Vervollständigen" }
            p { +"Eingeloggt als: "; b { +email } }
            
            form(action = "/profile/update", method = FormMethod.post) {
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
                
                button(type = ButtonType.submit) { +"Speichern" }
            }
        }
    }
}

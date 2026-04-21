package com.fiforum.views

import com.fiforum.models.UserData
import kotlinx.html.*

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

                div("input-group") {
                    label { 
                        input(type = InputType.checkBox) { 
                            name = "allowVCardDownload"; 
                            checked = user.allowVCardDownload; 
                            style = "width: auto; margin-right: 10px;" 
                        }
                        +"Teamkollegen dürfen meine Kontaktdaten (VCard) herunterladen"
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

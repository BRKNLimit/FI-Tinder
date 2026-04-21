package com.fiforum.views

import kotlinx.html.*

fun HTML.surveyPage(email: String) {
    layout("Survey // Matchmaker") {
        style {
            unsafe {
                raw("""
                    body, html { height: 100%; margin: 0; overflow: hidden; }
                    .survey-container {
                        display: flex;
                        flex-direction: column;
                        height: 100vh;
                        width: 100vw;
                        background: #000;
                    }
                    .survey-option {
                        flex: 1;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        cursor: pointer;
                        transition: background 0.3s;
                        font-family: 'VT323', monospace;
                        font-size: 2.5rem;
                        text-align: center;
                        padding: 20px;
                        text-transform: uppercase;
                    }
                    .option-a { border-bottom: 1px solid #333; color: #fff; }
                    .option-b { border-top: 1px solid #333; color: #fff; }
                    .option-a:hover, .option-b:hover { background: #111; }
                    
                    .question-center {
                        padding: 40px 20px;
                        text-align: center;
                        font-family: 'VT323', monospace;
                        font-size: 1.8rem;
                        font-weight: bold;
                        background: #000;
                        z-index: 10;
                        border-top: 2px solid #fff;
                        border-bottom: 2px solid #fff;
                    }
                    .progress-bar {
                        position: fixed;
                        top: 0; left: 0; height: 5px; background: var(--accent);
                        transition: width 0.3s;
                    }
                """)
            }
        }

        div("survey-container") {
            div("progress-bar") { id = "progressBar"; style = "width: 0%;" }
            
            div("survey-option option-a") {
                id = "optionA"
                onClick = "handleAnswer('A')"
                +"Option A"
            }
            
            div("question-center") {
                id = "questionText"
                +"Lade Fragen..."
            }
            
            div("survey-option option-b") {
                id = "optionB"
                onClick = "handleAnswer('B')"
                +"Option B"
            }
        }

        script {
            unsafe {
                raw("""
                    const questions = [
                        { q: "Was war das kulturell wichtigere Ereignis?", a: "Harambes Tod", b: "Timmys Strandung" },
                        { q: "Bist du von deiner Ausstrahlung her eher...", a: "Rot", b: "Blau" },
                        { q: "Welcher Planet wärst du?", a: "Saturn", b: "Uranus" },
                        { q: "Wo würdest du lieber wohnen?", a: "Auf dem Mars", b: "Auf dem Mond" },
                        { q: "Wenn du ein Sandkorn wärst...", a: "Am Strand", b: "Unter Wasser" },
                        { q: "Wärst du eher...", a: "Ein Jedi", b: "Ein Sith" },
                        { q: "Welche Größe hättest du lieber?", a: "10 cm groß", b: "10 Meter groß" },
                        { q: "Wenn du ein Baum wärst...", a: "Eine Palme", b: "Eine Eiche" },
                        { q: "Wärst du lieber ein...", a: "Girokonto", b: "Kreditkarte" },
                        { q: "Bist du von deiner Art her ein...", a: "Fahrrad", b: "Pedalo" }
                    ];

                    let currentStep = 0;
                    const answers = {};

                    function updateUI() {
                        const step = questions[currentStep];
                        document.getElementById('questionText').innerText = step.q;
                        document.getElementById('optionA').innerText = step.a;
                        document.getElementById('optionB').innerText = step.b;
                        document.getElementById('progressBar').style.width = ((currentStep / questions.length) * 100) + '%';
                    }

                    async function handleAnswer(choice) {
                        const step = questions[currentStep];
                        answers['q' + (currentStep + 1)] = (choice === 'A' ? step.a : step.b);
                        
                        currentStep++;
                        if (currentStep < questions.length) {
                            updateUI();
                        } else {
                            submitSurvey();
                        }
                    }

                    async function submitSurvey() {
                        document.querySelector('.survey-container').innerHTML = '<div style="color: white; font-family: VT323; font-size: 2rem; text-align: center; margin-top: 45vh;">ÜBERMITTLE ANTWORTEN...</div>';
                        
                        const response = await fetch('/submit-survey', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ email: '$email', ...answers })
                        });
                        
                        if (response.ok) {
                            window.location.href = '/myteam?email=' + encodeURIComponent('$email');
                        } else {
                            alert("Fehler beim Speichern!");
                        }
                    }

                    document.addEventListener('DOMContentLoaded', updateUI);
                """)
            }
        }
    }
}

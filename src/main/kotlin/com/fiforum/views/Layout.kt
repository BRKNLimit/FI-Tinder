package com.fiforum.views

import kotlinx.html.*

fun HTML.layout(title: String, headContent: HEAD.() -> Unit = {}, content: FlowContent.() -> Unit) {
    head {
        title(title)
        headContent()
        link(href = "https://fonts.googleapis.com/css2?family=VT323&family=Inter:wght@400;700&display=swap", rel = "stylesheet")
        style {
            unsafe {
                raw("""
                    :root {
                        --bg-color: #000000;
                        --text-primary: #ffffff;
                        --text-secondary: #a0a0a0;
                        --accent: #ff0000;
                        --border: 1px dashed #ffffff;
                    }
                    body {
                        background-color: var(--bg-color);
                        color: var(--text-primary);
                        font-family: 'Inter', sans-serif;
                        margin: 0;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        min-height: 100vh;
                        padding: 20px;
                    }
                    h1, h2, h3, .dot-matrix {
                        font-family: 'VT323', monospace;
                        text-transform: uppercase;
                        letter-spacing: 2px;
                    }
                    h1 { font-size: 3rem; margin-bottom: 0.5rem; }
                    .container {
                        max-width: 600px;
                        width: 100%;
                        border: var(--border);
                        padding: 30px;
                        margin-top: 50px;
                    }
                    .input-group {
                        margin-bottom: 20px;
                    }
                    label {
                        display: block;
                        font-size: 0.8rem;
                        color: var(--text-secondary);
                        text-transform: uppercase;
                        margin-bottom: 5px;
                    }
                    input, select {
                        width: 100%;
                        background: transparent;
                        border: 1px solid var(--text-secondary);
                        color: var(--text-primary);
                        padding: 10px;
                        font-family: inherit;
                        box-sizing: border-box;
                    }
                    input:focus {
                        border-color: var(--accent);
                        outline: none;
                    }
                    option {
                        background: #111111;
                        color: var(--text-primary);
                    }
                    button {
                        background: var(--text-primary);
                        color: var(--bg-color);
                        border: none;
                        padding: 15px 30px;
                        font-family: 'VT323', monospace;
                        font-size: 1.2rem;
                        text-transform: uppercase;
                        cursor: pointer;
                        width: 100%;
                        margin-top: 10px;
                        transition: background 0.2s;
                    }
                    button:hover {
                        background: var(--accent);
                        color: var(--text-primary);
                    }
                    .accent-text { color: var(--accent); }
                    .card {
                        border: 1px solid var(--text-secondary);
                        padding: 20px;
                        margin-bottom: 15px;
                    }
                    .badge {
                        display: inline-block;
                        border: 1px solid var(--text-secondary);
                        color: var(--text-secondary);
                        padding: 2px 8px;
                        font-size: 0.7rem;
                        text-transform: uppercase;
                        margin-right: 5px;
                        margin-bottom: 5px;
                    }
                    .badge-matched {
                        border: 1px solid var(--accent);
                        color: var(--accent);
                        background: rgba(255, 0, 0, 0.1);
                        font-weight: bold;
                    }
                    .spinner {
                        display: inline-block;
                        width: 20px;
                        height: 20px;
                        border: 2px dotted var(--accent);
                        border-radius: 50%;
                        animation: spin 2s linear infinite;
                    }
                    @keyframes spin {
                        0% { transform: rotate(0deg); }
                        100% { transform: rotate(360deg); }
                    }

                    /* Glitch Overlay */
                    .glitch-overlay {
                        position: fixed;
                        top: 0; left: 0; width: 100%; height: 100%;
                        background: #000;
                        z-index: 9999;
                        display: none;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        color: var(--accent);
                        font-family: 'VT323', monospace;
                        text-align: center;
                    }
                    .glitch-text {
                        font-size: 3rem;
                        text-transform: uppercase;
                        animation: glitch 0.2s infinite;
                    }
                    @keyframes glitch {
                        0% { transform: translate(0); text-shadow: -2px 0 red, 2px 0 blue; }
                        20% { transform: translate(-2px, 2px); }
                        40% { transform: translate(-2px, -2px); }
                        60% { transform: translate(2px, 2px); }
                        80% { transform: translate(2px, -2px); }
                        100% { transform: translate(0); }
                    }
                    .scanline {
                        width: 100%; height: 100px;
                        background: linear-gradient(rgba(255,0,0,0.1), transparent);
                        position: absolute;
                        top: -100px;
                        animation: scanline 2s linear infinite;
                    }
                    @keyframes scanline {
                        0% { top: -100px; }
                        100% { top: 100%; }
                    }

                    /* Flip Card System */
                    .id-card-perspective {
                        perspective: 1000px;
                        width: 400px;
                        max-width: 100%;
                        height: 250px;
                        margin: 20px auto;
                        cursor: pointer;
                    }
                    .id-card-inner {
                        position: relative;
                        width: 100%;
                        height: 100%;
                        text-align: center;
                        transition: transform 0.6s;
                        transform-style: preserve-3d;
                    }
                    .id-card-perspective.flipped .id-card-inner {
                        transform: rotateY(180deg);
                    }
                    .id-card-front, .id-card-back {
                        position: absolute;
                        width: 100%;
                        height: 100%;
                        -webkit-backface-visibility: hidden;
                        backface-visibility: hidden;
                        border: 1px solid #fff;
                        background: #000;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        box-sizing: border-box;
                    }
                    .id-card-back {
                        transform: rotateY(180deg);
                        flex-direction: column;
                        padding: 15px;
                    }
                    .grid-5x2 {
                        display: grid;
                        grid-template-columns: repeat(5, 1fr);
                        grid-template-rows: repeat(2, 1fr);
                        width: 100%;
                        height: 100%;
                        border: 1px dotted #333;
                    }
                    .grid-cell {
                        border: 0.5px dotted #333;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-family: 'VT323', monospace;
                        color: #222;
                        font-size: 0.8rem;
                    }
                """)
            }
        }
    }
    body {
        div("glitch-overlay") {
            id = "glitchOverlay"
            div("glitch-text") { +"MATCHING IN PROGRESS" }
            div { style = "margin-top: 10px; font-size: 1.2rem; color: var(--text-secondary);"; +"WIR STELLEN DAS BESTE TEAM ZUSAMMEN" }
            div { style = "margin-top: 20px;"; +"REBOOTING CORE..." }
            div { id = "glitchStatus"; style = "margin-top: 10px;"; +"0%" }
        }

        content()
    }
}

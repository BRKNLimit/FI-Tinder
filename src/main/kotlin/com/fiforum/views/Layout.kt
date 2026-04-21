package com.fiforum.views

import kotlinx.html.*

fun HTML.layout(title: String, headContent: HEAD.() -> Unit = {}, content: FlowContent.() -> Unit) {
    head {
        title(title)
        meta(name = "viewport", content = "width=device-width, initial-scale=1, maximum-scale=1")
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
                        padding: 10px;
                        box-sizing: border-box;
                    }
                    h1, h2, h3, .dot-matrix {
                        font-family: 'VT323', monospace;
                        text-transform: uppercase;
                        letter-spacing: 2px;
                    }
                    h1 { font-size: 2.5rem; margin-bottom: 0.5rem; text-align: center; }
                    .container {
                        max-width: 600px;
                        width: 100%;
                        border: var(--border);
                        padding: 20px;
                        margin-top: 20px;
                        box-sizing: border-box;
                    }
                    @media (max-width: 480px) {
                        h1 { font-size: 2rem; }
                        .container { padding: 15px; border-style: solid; }
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
                        padding: 12px;
                        font-family: inherit;
                        font-size: 1rem;
                        box-sizing: border-box;
                        border-radius: 0;
                    }
                    input:focus {
                        border-color: var(--accent);
                        outline: none;
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
                        border-radius: 0;
                    }
                    button:hover {
                        background: var(--accent);
                        color: var(--text-primary);
                    }
                    .card {
                        border: 1px solid var(--text-secondary);
                        padding: 15px;
                        margin-bottom: 15px;
                        box-sizing: border-box;
                    }
                    .badge {
                        display: inline-block;
                        border: 1px solid var(--text-secondary);
                        color: var(--text-secondary);
                        padding: 4px 10px;
                        font-size: 0.75rem;
                        text-transform: uppercase;
                        margin-right: 5px;
                        margin-bottom: 5px;
                    }
                    /* ... (rest of styles) */
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

package com.fiforum.views

import kotlinx.html.*

fun HTML.layout(title: String, content: FlowContent.() -> Unit) {
    head {
        title(title)
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
                        border: 1px solid var(--accent);
                        color: var(--accent);
                        padding: 2px 8px;
                        font-size: 0.7rem;
                        text-transform: uppercase;
                        margin-right: 5px;
                        margin-bottom: 5px;
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
                """)
            }
        }
    }
    body {
        content()
    }
}

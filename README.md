# FI-Tinder
# ⬛ Matchmaker // Event Teambuilding Engine

Ein minimalistisches, intelligentes Matchmaking-Tool für Tech-Events. Geschrieben in Kotlin/Ktor, verpackt im ikonischen "Nothing" OS Design.

## 🚀 Features

* **Smart Grouping Algorithm:** Setzt Teams von 4-5 Personen zusammen. Vermeidet Firmen-Klumpen (-100 Strafe) und maximiert gemeinsame Interessen (Tech-Stack, Hobbies, Workstyle).
* **Anti-Clique-Logik:** Verhindert, dass isolierte Paare am selben Tisch landen. Jeder am Tisch teilt Gemeinsamkeiten.
* **Auto-Naming:** Teams benennen sich dynamisch nach ihren stärksten Überschneidungen (z.B. *Team Cloud Computing & Kaffee-Junkie*).
* **Nothing OS Design:** Einzigartiges Dot-Matrix/Monochrom-Design mit roten Akzenten für maximalen Tech-Vibe.
* **Admin X-Ray:** Ein umfassendes Control-Panel zum Generieren von Mock-Daten, Starten des Matchings und Analysieren der Team-Zusammensetzungen.
* **Zero-DevOps:** Läuft out-of-the-box mit einer lokalen SQLite-Datenbank. Kein Server-Setup nötig.

## 🛠️ Tech Stack

* **Backend:** Kotlin, Ktor (Netty)
* **Database:** SQLite
* **ORM:** JetBrains Exposed
* **Frontend:** Ktor HTML DSL (Server-Side Rendering)
* **Styling:** Custom CSS (Nothing Brand UI)

## 📦 Local Setup & Run

1. Clone the repository:
   ```bash
   git clone [https://github.com/YOUR_NAME/matchmaker.git](https://github.com/YOUR_NAME/matchmaker.git)
   cd matchmaker

# Matchmaker Agent Rules

## 1. Architectural Rules
- Write clean, modular Kotlin code. Single Responsibility Principle applies.
- Never write monolithic files. Extract routes, database models, and HTML views into their respective packages (`routes`, `models`, `views`, `services`).
- Use JetBrains Exposed for database interactions wrapped in `transaction { ... }` blocks.
- Database must be SQLite (`jdbc:sqlite:./matchmaker.db`).

## 2. The Matching Algorithm (The Brain)
- **Goal:** Create teams of exactly 4 (remaining users form teams of 5).
- **Hard Constraint:** Users from the same `company` receive a massive penalty (-100 points). They should never be in the same team unless mathematically impossible.
- **Synergy:** Calculate points based on shared interests (+10 for tech/hobby, +5 for soft factors).
- **Anti-Clique Rule:** Ensure the algorithm penalizes teams where a single member has 0 overlaps with the rest of the team. We want interconnected graphs, not isolated pairs at the same table.
- **Dynamic Naming:** The algorithm must analyze the assigned team and name it based on their top 1-2 shared interests (e.g., "Team Cyber Security & Nachteule").

## 3. UI/UX Design System: "Nothing" OS Style
- **Aesthetic:** Minimalist, retro-futuristic, tech-heavy, monochrome with single accent colors.
- **Typography:** Use a Dot-Matrix / Pixel font for headings and numbers (import 'VT323' or 'DotGothic16' from Google Fonts). Use a clean sans-serif for body text.
- **Colors:** - Background: Pure Black (`#000000`) or very dark gray (`#111111`).
  - Text: White (`#ffffff`) and Light Gray (`#a0a0a0`).
  - Accents: "Nothing Red" (`#ff0000`).
- **Styling Details:**
  - Avoid soft shadows. Use hard borders (dotted or dashed) for cards.
  - Buttons should look like hardware toggles or terminal inputs.
  - Use uppercase text for labels and buttons.
  - Interactive elements (like shared interest badges) should use the red accent or inverted black/white styling.
  - Include a digital "dotted" spinner for the waiting room.

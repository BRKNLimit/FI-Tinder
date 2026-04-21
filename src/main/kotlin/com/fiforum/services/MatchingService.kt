package com.fiforum.services

import com.fiforum.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.minutes

object MatchingService {

    var isLaunched = false
    private var schedulerJob: Job? = null

    private val teamColors = listOf(
        "#ff0000", "#0066ff", "#00ff00", "#ffff00", "#9900ff", 
        "#ff9900", "#ff0099", "#00ffff", "#66ff00", "#00ff99"
    )

    fun calculatePairScore(u1: UserData, u2: UserData): Int {
        var score = 0
        if (u1.q1 == u2.q1 && u1.q1 != null) score += 1
        if (u1.q2 == u2.q2 && u1.q2 != null) score += 1
        if (u1.q3 == u2.q3 && u1.q3 != null) score += 1
        if (u1.q4 == u2.q4 && u1.q4 != null) score += 1
        if (u1.q5 == u2.q5 && u1.q5 != null) score += 1
        if (u1.q6 == u2.q6 && u1.q6 != null) score += 1
        if (u1.q7 == u2.q7 && u1.q7 != null) score += 1
        if (u1.q8 == u2.q8 && u1.q8 != null) score += 1
        if (u1.q9 == u2.q9 && u1.q9 != null) score += 1
        if (u1.q10 == u2.q10 && u1.q10 != null) score += 1
        
        // gleice firma is kacke also dicken abzug geben damit die nich zusammen kommen
        if (u1.company == u2.company && u1.company.isNotBlank()) score -= 100
        
        return score
    }

    private fun hasOverlap(u1: UserData, u2: UserData): Boolean {
        return (u1.q1 == u2.q1 || u1.q2 == u2.q2 || u1.q3 == u2.q3 || u1.q4 == u2.q4 || 
                u1.q5 == u2.q5 || u1.q6 == u2.q6 || u1.q7 == u2.q7 || u1.q8 == u2.q8 || 
                u1.q9 == u2.q9 || u1.q10 == u2.q10)
    }

    fun calculateTeamScore(members: List<UserData>): Int {
        var total = 0
        for (i in members.indices) {
            var userHasAnyOverlap = false
            for (j in members.indices) {
                if (i == j) continue
                val pairScore = calculatePairScore(members[i], members[j])
                total += pairScore
                if (hasOverlap(members[i], members[j])) userHasAnyOverlap = true
            }
            if (!userHasAnyOverlap && members.size > 1) total -= 50 
        }
        return total / 2 
    }

    fun runBatchMatching() {
        if (isLaunched) return

        transaction {
            val allUsers = Users.selectAll().where { Users.q1.isNotNull() }.map { it.toUserData() }
            if (allUsers.isEmpty()) return@transaction

            // erstma gruppen machen bevors richtig los geht mit dem optimieren
            var bestGlobalState = initialGrouping(allUsers)
            var bestGlobalScore = calculateGlobalScore(bestGlobalState)

            repeat(10) { 
                var currentState = initialGrouping(allUsers)
                var currentScore = calculateGlobalScore(currentState)
                var temperature = 100.0
                val coolingRate = 0.999
                
                repeat(5000) { 
                    val nextState = mutateState(currentState)
                    val nextScore = calculateGlobalScore(nextState)
                    val delta = nextScore - currentScore
                    // falls das besser is dann nehm wir das direkt, sons mit bissl glück
                    if (delta > 0 || Random.nextDouble() < Math.exp(delta / temperature)) {
                        currentState = nextState
                        currentScore = nextScore
                        if (currentScore > bestGlobalScore) {
                            bestGlobalState = currentState.map { it.toList() }.map { it.toMutableList() }
                            bestGlobalScore = currentScore
                        }
                    }
                    temperature *= coolingRate
                }
            }

            bestGlobalState.forEachIndexed { teamIndex, members ->
                val allAnswers = members.flatMap { 
                    listOf(it.q1, it.q2, it.q3, it.q4, it.q5, it.q6, it.q7, it.q8, it.q9, it.q10) 
                }.filterNotNull()
                
                val frequencies = allAnswers.groupingBy { it }.eachCount()
                val sortedAnswers = frequencies.entries.sortedByDescending { it.value }
                val top1 = sortedAnswers.getOrNull(0)?.key
                val top2 = sortedAnswers.getOrNull(1)?.key
                
                // hier die beeden besten antworten für den namen nuzen
                val teamName = generateCleverTeamName(top1, top2)
                
                val tId = TeamsTable.insert {
                    it[name] = teamName
                    it[mission1] = "Findet heraus, warum ihr alle '${top1 ?: "Dada"}' gewählt habt."
                    it[mission2] = "Diskutiert die kulturelle Relevanz von '${top2 ?: "Nichts"}'."
                    it[mission3] = "Erfindet einen Schlachtruf, der eure gemeinsame Liebe zu ${top1 ?: "Dada"} ausdrückt."
                    it[currentMissionIndex] = 1
                    it[teamColor] = teamColors[teamIndex % teamColors.size]
                }[TeamsTable.id]

                members.forEach { m -> Users.update({ Users.email eq m.email }) { it[teamId] = tId } }
            }
            isLaunched = true
            startMissionScheduler()
        }
    }

    private fun generateCleverTeamName(t1: String?, t2: String?): String {
        if (t1 == null) return "The Dadaist Collective"
        if (t2 == null) return "The $t1 Faction"

        val pair = if (t1 < t2) t1 to t2 else t2 to t1
        
        val names = mapOf(
            Pair("Harambes Tod", "Timmys Strandung") to "Die vergessenen Schlagzeilen",
            Pair("Harambes Tod", "Rot") to "Wut im Gehege",
            Pair("Harambes Tod", "Blau") to "Melancholische Primaten",
            Pair("Harambes Tod", "Saturn") to "Gorilla im Orbit",
            Pair("Harambes Tod", "Uranus") to "Kosmische Gerechtigkeit",
            Pair("Harambes Tod", "Auf dem Mars") to "Mission Harambe",
            Pair("Harambes Tod", "Auf dem Mond") to "Stille im Krater",
            Pair("Harambes Tod", "Am Strand") to "Urlaub vom Schicksal",
            Pair("Harambes Tod", "Unter Wasser") to "Tiefsee-Trauer",
            Pair("Harambes Tod", "Ein Jedi") to "Obi-Wan & Harambe",
            Pair("Harambes Tod", "Ein Sith") to "Die dunkle Seite des Zoos",
            Pair("Harambes Tod", "10 cm groß") to "Taschen-Märtyrer",
            Pair("Harambes Tod", "10 Meter groß") to "Giganten des Gedenkens",
            Pair("Harambes Tod", "Eine Palme") to "Dschungel-Requiem",
            Pair("Harambes Tod", "Eine Eiche") to "Stabile Ahnenforschung",
            Pair("Harambes Tod", "Girokonto") to "Nachlassverwalter",
            Pair("Harambes Tod", "Kreditkarte") to "Schulden bei der Evolution",
            Pair("Harambes Tod", "Dienstag") to "Dienstags im Gedenken",
            Pair("Harambes Tod", "Donnerstag") to "Die Donnerstags-Mahnwache (Gold)",

            Pair("Timmys Strandung", "Rot") to "Sonnenbrand-Alarm",
            Pair("Timmys Strandung", "Blau") to "Die Strandgut-Philister",
            Pair("Timmys Strandung", "Saturn") to "Interstellares Treibgut",
            Pair("Timmys Strandung", "Uranus") to "Gestrandet im Gasnebel",
            Pair("Timmys Strandung", "Auf dem Mars") to "Mars-Rover Bergungsteam",
            Pair("Timmys Strandung", "Auf dem Mond") to "Ebbe im Mare Tranquillitatis",
            Pair("Timmys Strandung", "Am Strand") to "Timmys Rettungsschwimmer (Gold)",
            Pair("Timmys Strandung", "Unter Wasser") to "Die Tiefsee-Archivare",
            Pair("Timmys Strandung", "Ein Jedi") to "Die Macht der Brandung",
            Pair("Timmys Strandung", "Ein Sith") to "Dunkle Wellenreiter",
            Pair("Timmys Strandung", "10 cm groß") to "Mikro-Castaways",
            Pair("Timmys Strandung", "10 Meter groß") to "Gestrandete Leuchttürme",
            Pair("Timmys Strandung", "Eine Palme") to "Robinson-Syndikat",
            Pair("Timmys Strandung", "Eine Eiche") to "Treibholz-Verband",
            Pair("Timmys Strandung", "Girokonto") to "Insolvenz am Ufer",
            Pair("Timmys Strandung", "Kreditkarte") to "Überzogene Muscheln",
            Pair("Timmys Strandung", "Dienstag") to "Dienstags-Flut",
            Pair("Timmys Strandung", "Donnerstag") to "Donnerstags-Wracks",

            Pair("Rot", "Blau") to "Die Lila-Fraktion",
            Pair("Rot", "Saturn") to "Der glühende Ring",
            Pair("Rot", "Uranus") to "Die Komplementär-Chaoten",
            Pair("Rot", "Auf dem Mars") to "Das rote Duplikat",
            Pair("Rot", "Auf dem Mond") to "Blutmond-Society",
            Pair("Rot", "Am Strand") to "Krebse auf Speed",
            Pair("Rot", "Unter Wasser") to "Kochendes Meer",
            Pair("Rot", "Ein Jedi") to "Das doppelte Lichtschwert",
            Pair("Rot", "Ein Sith") to "Zornige Buchhalter",
            Pair("Rot", "10 cm groß") to "Kleine Chilis",
            Pair("Rot", "10 Meter groß") to "Die wandelnden Zielscheiben (Gold)",
            Pair("Rot", "Eine Palme") to "Exotische Hitzewelle",
            Pair("Rot", "Eine Eiche") to "Herbst-Fanatiker",
            Pair("Rot", "Girokonto") to "Rote Zahlen",
            Pair("Rot", "Kreditkarte") to "Das Limit brennt",
            Pair("Rot", "Dienstag") to "Scharfer Dienstag",
            Pair("Rot", "Donnerstag") to "Donnerstag in Flammen",

            Pair("Blau", "Saturn") to "Frostige Ringe",
            Pair("Blau", "Uranus") to "Die doppelten Eisriesen",
            Pair("Blau", "Auf dem Mars") to "Die verwirrten Aliens (Gold)",
            Pair("Blau", "Auf dem Mond") to "Blue Moon Brigade",
            Pair("Blau", "Am Strand") to "Die Wellen-Ästheten",
            Pair("Blau", "Unter Wasser") to "Das tiefe Nichts",
            Pair("Blau", "Ein Jedi") to "Die Hüter der Ruhe",
            Pair("Blau", "Ein Sith") to "Kalte Wut",
            Pair("Blau", "10 cm groß") to "Schlümpfe auf Koks",
            Pair("Blau", "10 Meter groß") to "Die blauen Wolkenkratzer",
            Pair("Blau", "Eine Palme") to "Karibische Träume",
            Pair("Blau", "Eine Eiche") to "Gefrorenes Holz",
            Pair("Blau", "Girokonto") to "Liquiditäts-Blues",
            Pair("Blau", "Kreditkarte") to "Plastik-Ozean",
            Pair("Blau", "Dienstag") to "Der blaue Dienstag",
            Pair("Blau", "Donnerstag") to "Donnerstag im Nebel",

            Pair("Saturn", "Uranus") to "Die Gas-Giganten",
            Pair("Saturn", "Auf dem Mars") to "Orbitale Nachbarn",
            Pair("Saturn", "Auf dem Mond") to "Die Nachtwächter",
            Pair("Saturn", "Am Strand") to "Ringe im Sand",
            Pair("Saturn", "Unter Wasser") to "Druck im All",
            Pair("Saturn", "Ein Jedi") to "Galaktische Senatoren",
            Pair("Saturn", "Ein Sith") to "Imperiale Ringreiter",
            Pair("Saturn", "10 cm groß") to "Taschen-Planeten",
            Pair("Saturn", "10 Meter groß") to "Megalomanische Astrologen",
            Pair("Saturn", "Eine Palme") to "Tropischer Weltraum",
            Pair("Saturn", "Eine Eiche") to "Wurzeln im All",
            Pair("Saturn", "Girokonto") to "Kosmische Ersparnisse",
            Pair("Saturn", "Kreditkarte") to "Intergalaktische Schuldenfalle",
            Pair("Saturn", "Dienstag") to "Dienstags im Ring",
            Pair("Saturn", "Donnerstag") to "Donnerstags im Ring (Gold)",

            Pair("Uranus", "Auf dem Mars") to "Rote Gase",
            Pair("Uranus", "Auf dem Mond") to "Schlaflos im Orbit",
            Pair("Uranus", "Am Strand") to "Sand im Getriebe",
            Pair("Uranus", "Unter Wasser") to "Tiefsee-Atmosphäre",
            Pair("Uranus", "Ein Jedi") to "Die Macht des Methans",
            Pair("Uranus", "Ein Sith") to "Dunkle Gaswolken",
            Pair("Uranus", "10 cm groß") to "Winzige Unendlichkeit",
            Pair("Uranus", "10 Meter groß") to "Die titanen-Riege",
            Pair("Uranus", "Eine Palme") to "Strand am Abgrund",
            Pair("Uranus", "Eine Eiche") to "Eichen im Vakuum",
            Pair("Uranus", "Girokonto") to "Gasiges Erbe",
            Pair("Uranus", "Kreditkarte") to "Schulden am Limit",
            Pair("Uranus", "Dienstag") to "Gasmasken-Frühstück",
            Pair("Uranus", "Donnerstag") to "Donnerstags-Dunst",

            Pair("Auf dem Mars", "Auf dem Mond") to "Die Erdflüchtigen",
            Pair("Auf dem Mars", "Am Strand") to "Roter Sand",
            Pair("Auf dem Mars", "Unter Wasser") to "Kanäle der Sehnsucht",
            Pair("Auf dem Mars", "Ein Jedi") to "Verteidiger des roten Planeten",
            Pair("Auf dem Mars", "Ein Sith") to "Eroberer des Vulkans",
            Pair("Auf dem Mars", "10 cm groß") to "Mars-Männchen",
            Pair("Auf dem Mars", "10 Meter groß") to "Der rote Riese",
            Pair("Auf dem Mars", "Eine Palme") to "Mars-Oase",
            Pair("Auf dem Mars", "Eine Eiche") to "Rostiges Laub",
            Pair("Auf dem Mars", "Girokonto") to "Kolonie-Kasse",
            Pair("Auf dem Mars", "Kreditkarte") to "Weltraum-Zinsen",
            Pair("Auf dem Mars", "Dienstag") to "Mars-Dienstag",
            Pair("Auf dem Mars", "Donnerstag") to "Donnerstag im Staub",

            Pair("Auf dem Mond", "Am Strand") to "Die Gezeiten-Surfer",
            Pair("Auf dem Mond", "Unter Wasser") to "Die Gezeiten-Fraktion (Gold)",
            Pair("Auf dem Mond", "Ein Jedi") to "Ritter des fahlen Lichts",
            Pair("Auf dem Mond", "Ein Sith") to "Die dunkle Rückseite",
            Pair("Auf dem Mond", "10 cm groß") to "Mini-Astronauten",
            Pair("Auf dem Mond", "10 Meter groß") to "Der Mann im Mond",
            Pair("Auf dem Mond", "Eine Palme") to "Mondschein-Kokosnüsse",
            Pair("Auf dem Mond", "Eine Eiche") to "Der Schatten-Wald",
            Pair("Auf dem Mond", "Girokonto") to "Ebbe auf dem Konto",
            Pair("Auf dem Mond", "Kreditkarte") to "Mondpreise",
            Pair("Auf dem Mond", "Dienstag") to "Mond-Dienstag",
            Pair("Auf dem Mond", "Donnerstag") to "Der Donnerstag-Krater",

            Pair("Am Strand", "Unter Wasser") to "Die Schnorchler",
            Pair("Am Strand", "Ein Jedi") to "Sand-Allergiker",
            Pair("Am Strand", "Ein Sith") to "Darth Vader im Urlaub",
            Pair("Am Strand", "10 cm groß") to "Die Sandflöhe",
            Pair("Am Strand", "10 Meter groß") to "Strand-Wächter",
            Pair("Am Strand", "Eine Palme") to "Das Standard-Postkarten-Team",
            Pair("Am Strand", "Eine Eiche") to "Wald am Meer",
            Pair("Am Strand", "Girokonto") to "Sand in der Kasse",
            Pair("Am Strand", "Kreditkarte") to "Kurtaxen-Preller",
            Pair("Am Strand", "Dienstag") to "Dienstags am Meer",
            Pair("Am Strand", "Donnerstag") to "Donnerstags-Sonnenbrand",

            Pair("Unter Wasser", "Ein Jedi") to "Gungan-Verbündete",
            Pair("Unter Wasser", "Ein Sith") to "Haie der Unterwelt",
            Pair("Unter Wasser", "10 cm groß") to "Mikro-Plankton",
            Pair("Unter Wasser", "10 Meter groß") to "Der Kraken-Klub",
            Pair("Unter Wasser", "Eine Palme") to "Unterwasser-Urlaub",
            Pair("Unter Wasser", "Eine Eiche") to "Korallen-Wald",
            Pair("Unter Wasser", "Girokonto") to "Eingefrorene Konten",
            Pair("Unter Wasser", "Kreditkarte") to "Liquiditätsengpass (Gold)",
            Pair("Unter Wasser", "Dienstag") to "U-Boot Dienstag",
            Pair("Unter Wasser", "Donnerstag") to "Donnerstagstaucher",

            Pair("Ein Jedi", "Ein Sith") to "Das instabile Gleichgewicht",
            Pair("Ein Jedi", "10 cm groß") to "Meister Yoda Format",
            Pair("Ein Jedi", "10 Meter groß") to "Die hohen Ratsmitglieder",
            Pair("Ein Jedi", "Eine Palme") to "Padawane unter Palmen",
            Pair("Ein Jedi", "Eine Eiche") to "Die Weisen des Waldes",
            Pair("Ein Jedi", "Girokonto") to "Yodas Bausparer (Gold)",
            Pair("Ein Jedi", "Kreditkarte") to "Die Macht des Konsums",
            Pair("Ein Jedi", "Dienstag") to "Jedi-Rat am Dienstag",
            Pair("Ein Jedi", "Donnerstag") to "Donnerstags-Lichtschwert",

            Pair("Ein Sith", "10 cm groß") to "Aggressive Taschenlampen",
            Pair("Ein Sith", "10 Meter groß") to "Imperiale Giganten",
            Pair("Ein Sith", "Eine Palme") to "Tropische Tyrannen",
            Pair("Ein Sith", "Eine Eiche") to "Die dunkle Wurzel",
            Pair("Ein Sith", "Girokonto") to "Darth Dispo (Gold)",
            Pair("Ein Sith", "Kreditkarte") to "Unbegrenzte Macht (auf Pump)",
            Pair("Ein Sith", "Dienstag") to "Darth Dienstags",
            Pair("Ein Sith", "Donnerstag") to "Lord Donnerstag",

            Pair("10 cm groß", "10 Meter groß") to "Die Paradoxen",
            Pair("10 cm groß", "Eine Palme") to "Mini-Kokosnüsse",
            Pair("10 cm groß", "Eine Eiche") to "Die Bonsai-Holzfäller (Gold)",
            Pair("10 cm groß", "Girokonto") to "Taschengeld-Kaiser",
            Pair("10 cm groß", "Kreditkarte") to "Die Kleingeld-Mafia",
            Pair("10 cm groß", "Dienstag") to "Dienstag im Westentaschen-Format",
            Pair("10 cm groß", "Donnerstag") to "Der kleine Donnerstag",

            Pair("10 Meter groß", "Eine Palme") to "Die Riesen-Wedel",
            Pair("10 Meter groß", "Eine Eiche") to "Urwald-Giganten",
            Pair("10 Meter groß", "Girokonto") to "Große Erwartungen",
            Pair("10 Meter groß", "Kreditkarte") to "XXL-Verschuldung",
            Pair("10 Meter groß", "Dienstag") to "Der lange Dienstag",
            Pair("10 Meter groß", "Donnerstag") to "Gigantischer Donnerstag",

            Pair("Eine Palme", "Eine Eiche") to "Das Hybrid-Holz",
            Pair("Eine Palme", "Girokonto") to "Urlaubskasse",
            Pair("Eine Palme", "Kreditkarte") to "Karibische Ratenzahlung",
            Pair("Eine Palme", "Dienstag") to "Kokos-Dienstag",
            Pair("Eine Palme", "Donnerstag") to "Ferien am Abgrund",

            Pair("Eine Eiche", "Girokonto") to "Festverzinsliche Werte",
            Pair("Eine Eiche", "Kreditkarte") to "Holziges Plastik",
            Pair("Eine Eiche", "Dienstag") to "Stabiler Dienstag",
            Pair("Eine Eiche", "Donnerstag") to "Eichen-Donnerstag",

            Pair("Girokonto", "Kreditkarte") to "Die Schufa-Opfer",
            Pair("Girokonto", "Dienstag") to "Zahltag-Dienstag",
            Pair("Girokonto", "Donnerstag") to "Buchungs-Donnerstag",

            Pair("Kreditkarte", "Dienstag") to "Shopping-Dienstag",
            Pair("Kreditkarte", "Donnerstag") to "Inkasso-Donnerstag",

            Pair("Dienstag", "Donnerstag") to "Wochenmitte-Vakuum"
        )

        // hier beede richtungen checken damit der name auch wirlich passt
        return names[Pair(t1, t2)] ?: names[Pair(t2, t1)] ?: "Team $t1 & $t2"
    }

    private fun startMissionScheduler() {
        schedulerJob?.cancel()
        schedulerJob = CoroutineScope(Dispatchers.IO).launch {
            delay(15.minutes)
            incrementMissions()
            delay(15.minutes)
            incrementMissions()
        }
    }

    private suspend fun incrementMissions() {
        transaction {
            val teams = TeamsTable.selectAll().map { it[TeamsTable.id] }
            teams.forEach { tId ->
                TeamsTable.update({ TeamsTable.id eq tId }) {
                    with(SqlExpressionBuilder) {
                        it.update(currentMissionIndex, currentMissionIndex + 1)
                    }
                }
            }
        }
        MatchingSocketService.broadcastNewMission()
    }

    fun assignLatecomer(latecomer: UserData): Int? {
        return transaction {
            val teams = TeamsTable.selectAll().map { it[TeamsTable.id] }
            if (teams.isEmpty()) return@transaction null
            val candidateTeams = teams.map { tId ->
                val members = Users.selectAll().where { Users.teamId eq tId }.map { it.toUserData() }
                tId to members
            }
            val bestTeam = candidateTeams.filter { it.second.size < 6 }
                .maxByOrNull { (_, members) -> calculateTeamScore(members + latecomer) }
            bestTeam?.let { (tId, _) ->
                Users.update({ Users.email eq latecomer.email }) { it[teamId] = tId }
                tId
            }
        }
    }

    private fun initialGrouping(users: List<UserData>): List<List<UserData>> {
        val shuffled = users.shuffled()
        val chunked = shuffled.chunked(4)
        if (chunked.isEmpty()) return emptyList()
        val result = chunked.map { it.toMutableList() }.toMutableList()
        if (result.last().size < 3 && result.size > 1) {
            val last = result.removeAt(result.size - 1)
            last.forEachIndexed { index, userData -> result[index % result.size].add(userData) }
        }
        return result
    }

    private fun calculateGlobalScore(state: List<List<UserData>>): Int = state.sumOf { calculateTeamScore(it) }

    private fun mutateState(state: List<List<UserData>>): List<List<UserData>> {
        val newState = state.map { it.toMutableList() }.toMutableList()
        if (newState.size < 2) return newState
        val t1Idx = Random.nextInt(newState.size); val t2Idx = Random.nextInt(newState.size)
        if (t1Idx == t2Idx) return newState
        val team1 = newState[t1Idx]; val team2 = newState[t2Idx]
        if (team1.isNotEmpty() && team2.isNotEmpty()) {
            val u1Idx = Random.nextInt(team1.size); val u2Idx = Random.nextInt(team2.size)
            val u1 = team1[u1Idx]; team1[u1Idx] = team2[u2Idx]; team2[u2Idx] = u1
        }
        return newState
    }
}

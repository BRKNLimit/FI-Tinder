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
        "#ff0000", // Red
        "#0066ff", // Blue
        "#00ff00", // Green
        "#ffff00", // Yellow
        "#9900ff", // Purple
        "#ff9900", // Orange
        "#ff0099", // Pink
        "#00ffff", // Cyan
        "#66ff00", // Lime
        "#00ff99"  // Teal
    )

    private val sportGroup = setOf("Fußball", "Wandern", "Yoga", "Gym / Fitness", "Teamsport", "ab zum Sport", "Sportergebnisse")
    private val gamingGroup = setOf("Gaming", "Zocken", "Gaming News")
    private val foodGroup = setOf("Kochen", "Fancy kochen", "Snacks")
    private val travelGroup = setOf("Reisen", "Urlaubspläne", "Asien", "Nordamerika", "Südamerika", "Südeuropa", "Skandinavien", "Hauptsache warm", "Hauptsache Action", "Australien", "Afrika", "Städtetrip", "Roadtrip")
    private val chillGroup = setOf("Balkonien", "ab auf die Couch", "Filme und Serien", "Tee", "Wasser (stay hydrated)")

    fun calculatePairScore(u1: UserData, u2: UserData): Int {
        var score = 0
        // Synergy: +10 for tech/hobby, +5 for soft factors
        if (u1.hobby == u2.hobby && u1.hobby.isNotBlank()) score += 10
        if (u1.techInterest == u2.techInterest && u1.techInterest.isNotBlank()) score += 10
        if (u1.travel == u2.travel && u1.travel.isNotBlank()) score += 5
        if (u1.workstyle == u2.workstyle && u1.workstyle.isNotBlank()) score += 5
        if (u1.coffeeTalk == u2.coffeeTalk && u1.coffeeTalk.isNotBlank()) score += 5
        if (u1.afterWork == u2.afterWork && u1.afterWork.isNotBlank()) score += 5
        if (u1.popculture == u2.popculture && u1.popculture.isNotBlank()) score += 5
        if (u1.fuel == u2.fuel && u1.fuel.isNotBlank()) score += 5
        
        // Hard Constraint: Massive penalty for same company
        if (u1.company == u2.company && u1.company.isNotBlank()) score -= 100
        
        return score
    }

    private fun hasOverlap(u1: UserData, u2: UserData): Boolean {
        if (u1.hobby == u2.hobby && u1.hobby.isNotBlank()) return true
        if (u1.techInterest == u2.techInterest && u1.techInterest.isNotBlank()) return true
        if (u1.travel == u2.travel && u1.travel.isNotBlank()) return true
        if (u1.workstyle == u2.workstyle && u1.workstyle.isNotBlank()) return true
        if (u1.coffeeTalk == u2.coffeeTalk && u1.coffeeTalk.isNotBlank()) return true
        if (u1.afterWork == u2.afterWork && u1.afterWork.isNotBlank()) return true
        if (u1.popculture == u2.popculture && u1.popculture.isNotBlank()) return true
        if (u1.fuel == u2.fuel && u1.fuel.isNotBlank()) return true
        return false
    }

    fun calculateTeamScore(members: List<UserData>): Int {
        var total = 0
        for (i in members.indices) {
            var userHasAnyOverlap = false
            for (j in members.indices) {
                if (i == j) continue
                val pairScore = calculatePairScore(members[i], members[j])
                total += pairScore
                if (hasOverlap(members[i], members[j])) {
                    userHasAnyOverlap = true
                }
            }
            // Anti-Clique Rule: Heavy penalty for isolated members
            if (!userHasAnyOverlap && members.size > 1) {
                total -= 200 
            }
        }
        return total / 2 
    }

    fun runBatchMatching() {
        if (isLaunched) return

        transaction {
            val allUsers = Users.selectAll().map { it.toUserData() }

            if (allUsers.isEmpty()) return@transaction

            // --- GLOBAL OPTIMIZATION (Simulated Annealing) ---
            var bestGlobalState = initialGrouping(allUsers)
            var bestGlobalScore = calculateGlobalScore(bestGlobalState)

            repeat(15) { 
                var currentState = initialGrouping(allUsers)
                var currentScore = calculateGlobalScore(currentState)
                
                var temperature = 100.0
                val coolingRate = 0.9995
                
                repeat(10000) { 
                    val nextState = mutateState(currentState)
                    val nextScore = calculateGlobalScore(nextState)
                    
                    val delta = nextScore - currentScore
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

            // Save results
            bestGlobalState.forEachIndexed { teamIndex, members ->
                val allInterests = members.flatMap { 
                    listOf(it.hobby, it.techInterest, it.travel, it.workstyle, it.coffeeTalk, it.afterWork, it.fuel) 
                }.filter { it.isNotBlank() && !it.startsWith("...") }
                
                val frequencies = allInterests.groupingBy { it }.eachCount()
                val sortedInterests = frequencies.entries.sortedByDescending { it.value }
                val top1 = sortedInterests.getOrNull(0)?.key
                val top2 = sortedInterests.getOrNull(1)?.key
                
                val teamName = generateCleverTeamName(top1, top2)
                
                // STEP 4: Icebreaker Missions
                val missions = when {
                    top1 in gamingGroup || top2 in gamingGroup -> listOf(
                        "Hand aufs Herz: Welches Game hat euch in der Schulzeit oder im Studium bisher die meisten Stunden Schlaf gekostet?",
                        "Was ist der am meisten überbewertete Tech-Trend oder Hype im Moment?",
                        "Einigt euch auf das ultimative 'Survival-Game' für einen langweiligen Berufsschul- oder Vorlesungstag."
                    )
                    top1 in sportGroup || top2 in sportGroup -> listOf(
                        "Wenn ihr den Rest eures Lebens nur noch eine einzige Sportart machen (oder schauen) dürftet, welche wäre es?",
                        "Welche Fitness- oder Ernährungs-Weisheit ist in euren Augen absoluter Quatsch?",
                        "Findet heraus, wer von euch den kürzesten Weg zum Gym/Sportverein hat und wer den absolut verrücktesten Muskelkater-Moment seines Lebens hatte."
                    )
                    top1 in foodGroup || top2 in foodGroup -> listOf(
                        "Es ist 19 Uhr, ihr kommt platt aus dem Büro oder der Uni. Was ist euer absolutes 15-Minuten-Lebensretter-Rezept?",
                        "Welcher klassische Büro-Snack oder welches Mensa-Essen wird von allen geliebt, ist aber eigentlich furchtbar?",
                        "Teilt euer bestes 'Studenten-Budget'-Rezept, das trotzdem so schmeckt, als käme es aus einem schicken Restaurant."
                    )
                    top1 in travelGroup || top2 in travelGroup -> listOf(
                        "Sobald das erste richtige Vollzeitgehalt auf dem Konto ist: Welcher Trip steht ganz oben auf der Bucketlist?",
                        "Strandurlaub mit All-Inclusive oder Backpacking mit dem Rucksack – was ist der wahre Urlaub?",
                        "Erzählt euch gegenseitig von eurem absolut schlimmsten 'Reise-Fail' (verpasster Flug, verlorenes Gepäck, furchtbares Airbnb)."
                    )
                    top1 in chillGroup || top2 in chillGroup -> listOf(
                        "Welche Serie könnt ihr immer wieder von vorne anfangen, ohne dass sie langweilig wird?",
                        "Ist 'Snoozen' am Morgen die beste Erfindung der Menschheit oder pure Selbstquälerei?",
                        "Tauscht eure besten Lifehacks aus, wie man an einem 'Remote'- oder 'Balkonien'-Tag maximal entspannt, aber auf Slack/Teams trotzdem produktiv aussieht."
                    )
                    else -> listOf(
                        "Welches Klischee über ITler oder BWLer erfüllt ihr zu 100 % und welches so gar nicht?",
                        "Versucht in genau 2 Minuten herauszufinden, was ihr (abgeshen von eurem Arbeitgeber) als absolute Gemeinsamkeit habt.",
                        "Wenn ihr eine neue Programmiersprache oder ein neues Framework erfinden müsstet, wie hieße es und was wäre das Killer-Feature?"
                    )
                }.shuffled()

                val tId = TeamsTable.insert {
                    it[name] = teamName
                    it[mission1] = missions.getOrNull(0)
                    it[mission2] = missions.getOrNull(1)
                    it[mission3] = missions.getOrNull(2)
                    it[currentMissionIndex] = 1
                    it[teamColor] = teamColors[teamIndex % teamColors.size]
                }[TeamsTable.id]

                members.forEach { m -> Users.update({ Users.email eq m.email }) { it[teamId] = tId } }
            }
            isLaunched = true
            startMissionScheduler()
        }
    }

    private fun startMissionScheduler() {
        schedulerJob?.cancel()
        // Use a background scope to manage mission rotation
        schedulerJob = CoroutineScope(Dispatchers.IO).launch {
            // Wait 15 minutes for the 2nd mission
            delay(15.minutes)
            incrementMissions()
            
            // Wait another 15 minutes for the 3rd mission
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

    private fun generateCleverTeamName(t1: String?, t2: String?): String {
        if (t1 == null) return "The Allrounders"
        if (t2 == null) return "The $t1 Squad"

        // STEP 1: SYNERGY OVERRIDES
        val synergyNames = when {
            t1 in sportGroup && t2 in sportGroup -> listOf("The Athletics", "The Sweat Equity", "The Varsity Squad")
            t1 in gamingGroup && t2 in gamingGroup -> listOf("The Tryhards", "The Cyber Syndicate", "The Final Bosses")
            t1 in foodGroup && t2 in foodGroup -> listOf("The Culinary Cartel", "The Flavor Squad", "The Tastemakers")
            t1 in travelGroup && t2 in travelGroup -> listOf("The Globetrotters", "The Mileage Club", "The Wayfarers")
            t1 in chillGroup && t2 in chillGroup -> listOf("The Zen Masters", "The Couch Cartel", "The Low-Battery Squad")
            else -> null
        }

        if (synergyNames != null) return synergyNames.random()

        // STEP 2: WORD MAPPING
        val prefixes = mapOf(
            "Kaffee" to "Caffeine", "Energy Drinks" to "High-Voltage", "Mate" to "Mate",
            "Spezi / Cola" to "Sugar", "Tee" to "Zen", "Wasser (stay hydrated)" to "Hydro",
            "Snacks" to "Crumb", "Remote" to "Pajama", "im Office" to "Desk",
            "Hybrid" to "Flex", "möglichst früh" to "Early-Bird", "möglichst spät" to "Night-Owl",
            "Programmieren" to "Code", "AI" to "Prompt", "Cloud" to "Cloud",
            "Cyber Security" to "Firewall", "BlockChain" to "Crypto", "Devops" to "Pipeline",
            "Data Science" to "Data", "FinTech" to "Cashflow", "Agile/Scrum" to "Sprint",
            "Business Intelligence" to "Dashboard", "UX/UI Design" to "Pixel",
            "Projektmanagement" to "Gantt", "E-Commerce" to "Cart"
        )

        val suffixes = mapOf(
            "Fußball" to "Strikers", "Wandern" to "Trailblazers", "Kochen" to "Gourmets",
            "Gaming" to "Looters", "Lesen" to "Bookworms", "Reisen" to "Nomads",
            "Fotografie" to "Focus-Ninjas", "Musik" to "Beatmakers", "Yoga" to "Gurus",
            "Malen" to "Creators", "Gym / Fitness" to "Lifters", "Teamsport" to "Team-Players",
            "Asien" to "Far-East Fans", "Nordamerika" to "Overseas Explorers",
            "Südamerika" to "Latino Lovers", "Südeuropa" to "Siesta Seekers",
            "Skandinavien" to "Nordic Hunters", "Hauptsache warm" to "Sun Chasers",
            "Hauptsache Action" to "Adrenaline Junkies", "Australien" to "Down-Under Dudes",
            "Afrika" to "Safari Squad", "Städtetrip" to "Asphalt Cowboys",
            "Roadtrip" to "Mile Makers", "Balkonien" to "Homebodies",
            "Filme und Serien" to "Binge-Watchers", "Tech Gossip" to "Rumor Millers",
            "Krypto und Finanzen" to "Brokers", "Sportergebnisse" to "Tacticians",
            "Haustier und Alltag" to "Pet-Fluencers", "Urlaubspläne" to "Tour Guides",
            "Gaming News" to "Nerds", "Lokale Events" to "Party Planners",
            "Studium & Berufsschule" to "Campus Legends", "Feierabendbier" to "Tap Heroes",
            "ab zum Sport" to "Endorphin Junkies", "ab auf die Couch" to "Couch Potatoes",
            "Side Hustle" to "Hustlers", "Fancy kochen" to "Star Chefs",
            "Freunde treffen" to "Socializers", "Zocken" to "Boss Slayers"
        )

        // STEP 3: COMBINATION RULES
        val p1 = prefixes[t1]
        val p2 = prefixes[t2]
        val s1 = suffixes[t1]
        val s2 = suffixes[t2]

        return when {
            // Rule 1: Prefix + Suffix
            p1 != null && s2 != null -> "The $p1 $s2"
            p2 != null && s1 != null -> "The $p2 $s1"
            
            // Rule 2: Prefix + Prefix
            p1 != null && p2 != null -> "The $p1-$p2 ${listOf("Squad", "Faction").random()}"
            
            // Rule 3: Suffix + Suffix
            s1 != null && s2 != null -> "The $s1-$s2"
            
            // Fallback
            else -> "The $t1 & $t2 Synergy"
        }
    }

    fun assignLatecomer(latecomer: UserData): Int? {
        return transaction {
            val teams = TeamsTable.selectAll().map { it[TeamsTable.id] }
            if (teams.isEmpty()) return@transaction null

            val candidateTeams = teams.map { tId ->
                val members = Users.selectAll().where { Users.teamId eq tId }.map { it.toUserData() }
                tId to members
            }

            // Assign Latecomers based on best synergy, regardless of absolute team size parity
            val bestTeam = candidateTeams
                .filter { it.second.size < 6 } // Safety cap of 6 members
                .maxByOrNull { (_, members) ->
                    // Priority 1: Match quality (Team synergy)
                    // Priority 2: Prefer teams of 4 over teams of 5 (slight penalty for larger teams)
                    val sizePenalty = if (members.size >= 5) 40 else 0
                    calculateTeamScore(members + latecomer) - sizePenalty
                }

            bestTeam?.let { (tId, _) ->
                Users.update({ Users.email eq latecomer.email }) { it[teamId] = tId }
                tId
            }
        }
    }

    private fun initialGrouping(users: List<UserData>): List<List<UserData>> {
        val shuffled = users.shuffled()
        val teams = mutableListOf<MutableList<UserData>>()
        val chunked = shuffled.chunked(4)
        if (chunked.isEmpty()) return emptyList()
        
        val result = chunked.map { it.toMutableList() }.toMutableList()
        if (result.last().size < 4 && result.size > 1) {
            val last = result.removeAt(result.size - 1)
            last.forEachIndexed { index, userData ->
                result[index % result.size].add(userData)
            }
        }
        return result
    }

    private fun calculateGlobalScore(state: List<List<UserData>>): Int {
        return state.sumOf { calculateTeamScore(it) }
    }

    private fun mutateState(state: List<List<UserData>>): List<List<UserData>> {
        val newState = state.map { it.toMutableList() }.toMutableList()
        if (newState.size < 2) return newState
        val t1Idx = Random.nextInt(newState.size)
        val t2Idx = Random.nextInt(newState.size)
        if (t1Idx == t2Idx) return newState
        val team1 = newState[t1Idx]
        val team2 = newState[t2Idx]
        if (team1.isNotEmpty() && team2.isNotEmpty()) {
            val u1Idx = Random.nextInt(team1.size)
            val u2Idx = Random.nextInt(team2.size)
            val u1 = team1[u1Idx]
            team1[u1Idx] = team2[u2Idx]
            team2[u2Idx] = u1
        }
        return newState
    }
}

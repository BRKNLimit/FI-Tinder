package com.fiforum.services

import com.fiforum.models.TeamsTable
import com.fiforum.models.UserData
import com.fiforum.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random

object MatchingService {

    var isLaunched = false

    fun calculatePairScore(u1: UserData, u2: UserData): Int {
        var score = 0
        if (u1.hobby == u2.hobby && u1.hobby.isNotBlank()) score += 10
        if (u1.techInterest == u2.techInterest && u1.techInterest.isNotBlank()) score += 10
        if (u1.travel == u2.travel && u1.travel.isNotBlank()) score += 5
        if (u1.workstyle == u2.workstyle && u1.workstyle.isNotBlank()) score += 5
        if (u1.coffeeTalk == u2.coffeeTalk && u1.coffeeTalk.isNotBlank()) score += 5
        if (u1.afterWork == u2.afterWork && u1.afterWork.isNotBlank()) score += 5
        if (u1.popculture == u2.popculture && u1.popculture.isNotBlank()) score += 5
        if (u1.fuel == u2.fuel && u1.fuel.isNotBlank()) score += 5
        
        // AGGRESSIVE Penalty for same company
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
            // Anti-Clique Rule: Massive penalty if a user has 0 overlaps with anyone else in the team
            if (!userHasAnyOverlap && members.size > 1) {
                total -= 100 // Increased from 50 to be more decisive
            }
        }
        return total / 2 
    }

    fun runBatchMatching() {
        if (isLaunched) return

        transaction {
            val allUsers = Users.selectAll().map {
                UserData(it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel])
            }

            if (allUsers.isEmpty()) return@transaction

            // --- GLOBAL OPTIMIZATION (Simulated Annealing) ---
            var bestGlobalState = initialGrouping(allUsers)
            var bestGlobalScore = calculateGlobalScore(bestGlobalState)

            repeat(15) { // 15 Random Restarts for broader search
                var currentState = initialGrouping(allUsers)
                var currentScore = calculateGlobalScore(currentState)
                
                var temperature = 100.0
                val coolingRate = 0.9995
                
                repeat(10000) { // 10000 Iterations per restart
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
            bestGlobalState.forEach { members ->
                val allInterests = members.flatMap { 
                    listOf(it.hobby, it.techInterest, it.travel, it.workstyle, it.coffeeTalk, it.afterWork, it.fuel) 
                }.filter { it.isNotBlank() && !it.startsWith("...") }
                
                val frequencies = allInterests.groupingBy { it }.eachCount()
                val sortedInterests = frequencies.entries.sortedByDescending { it.value }
                val top1 = sortedInterests.getOrNull(0)?.key
                val top2 = sortedInterests.getOrNull(1)?.key
                
                val teamName = generateCleverTeamName(top1, top2)

                val tId = TeamsTable.insert {
                    it[name] = teamName
                }[TeamsTable.id]

                members.forEach { m -> Users.update({ Users.email eq m.email }) { it[teamId] = tId } }
            }
            isLaunched = true
        }
    }

    private fun generateCleverTeamName(t1: String?, t2: String?): String {
        if (t1 == null) return "Die Allrounder"
        if (t2 == null) return "Project $t1"

        val pair = if (t1 < t2) t1 to t2 else t2 to t1
        
        return when (pair) {
            "AI" to "Gaming" -> "Neural Highscore Heroes"
            "AI" to "Kotlin" -> "Null-Safe Intelligence"
            "Backend" to "Kotlin" -> "The Robust Server-Side"
            "Blockchain" to "Börse & Krypto" -> "The Decentralized Miners"
            "Kaffee" to "Tech-Gossip" -> "Brewed Debugging Logic"
            "Kochen" to "Fancy Kochen" -> "The Gourmet Architects"
            "Fußball" to "Sport-Ergebnisse" -> "Pitch Analyst Squad"
            "Gaming" to "Gaming News" -> "The Highscore Legends"
            "Cloud" to "DevOps" -> "Automated Sky-Net"
            "Cyber Security" to "Blockchain" -> "Immutable Guardians"
            "Filme & Serien" to "Ab auf die Couch" -> "The Binge Protocol"
            "Musik" to "After Work" -> "Sonic Chill-Out Zone"
            "Gaming" to "Energy Drinks" -> "The Overclocked Gamers"
            "Wandern" to "Hauptsache Action" -> "Summit Chasers"
            "Lesen" to "Tee" -> "The Steeped Thinkers"
            "Frontend" to "Design" -> "The Pixel Perfectionists"
            
            else -> {
                val patterns = listOf(
                    "The $t1 $t2 Collective",
                    "Operation $t1-$t2",
                    "Nexus of $t1 and $t2",
                    "$t1 x $t2 Synergy",
                    "The $t1 $t2 Alliance"
                )
                patterns.random()
            }
        }
    }

    fun assignLatecomer(latecomer: UserData): Int? {
        return transaction {
            val teams = TeamsTable.selectAll().map { it[TeamsTable.id] }
            if (teams.isEmpty()) return@transaction null

            val candidateTeams = teams.map { tId ->
                val members = Users.select { Users.teamId eq tId }.map {
                    UserData(it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel])
                }
                tId to members
            }

            // Prioritize teams of 4, then 5, etc.
            val minSize = candidateTeams.minOf { it.second.size }
            val smallestTeams = candidateTeams.filter { it.second.size == minSize }

            val bestTeam = smallestTeams.maxByOrNull { (_, members) ->
                calculateTeamScore(members + latecomer)
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
        
        // Goal: teams of exactly 4 (remaining users form teams of 5)
        // If we have N users, N / 4 teams.
        // N = 4k + r. r is 0, 1, 2, 3.
        // If r=1, one team of 5.
        // If r=2, two teams of 5 (or one of 6, but 5 is better).
        // If r=3, three teams of 5.
        
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
        val t1Idx = Random.nextInt(newState.size)
        val t2Idx = Random.nextInt(newState.size)
        if (t1Idx == t2Idx) return newState

        val team1 = newState[t1Idx]
        val team2 = newState[t2Idx]

        if (team1.isNotEmpty() && team2.isNotEmpty()) {
            val u1Idx = Random.nextInt(team1.size)
            val u2Idx = Random.nextInt(team2.size)
            
            // SWAP
            val u1 = team1[u1Idx]
            team1[u1Idx] = team2[u2Idx]
            team2[u2Idx] = u1
        }
        return newState
    }
}

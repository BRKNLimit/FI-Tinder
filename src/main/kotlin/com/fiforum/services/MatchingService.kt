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
            val allUsers = Users.selectAll().map {
                UserData(it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel])
            }

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
        if (t1 == null) return "Team Allrounder"
        if (t2 == null) return "Team $t1"

        return "Team $t1 & $t2"
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

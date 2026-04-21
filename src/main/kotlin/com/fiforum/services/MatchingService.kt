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
        
        // Exact matches on Dadaist questions
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
        
        // Hard Constraint: Penalty for same company
        if (u1.company == u2.company && u1.company.isNotBlank()) score -= 100
        
        return score
    }

    private fun hasOverlap(u1: UserData, u2: UserData): Boolean {
        // Any shared answer is an overlap
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
                val topAnswers = frequencies.entries.sortedByDescending { it.value }.take(2).map { it.key }
                
                val teamName = if (topAnswers.size >= 2) {
                    "Team ${topAnswers[0]} & ${topAnswers[1]}"
                } else {
                    "The Dadaist Collective"
                }
                
                val tId = TeamsTable.insert {
                    it[name] = teamName
                    it[mission1] = "Findet heraus, warum ihr alle '${topAnswers.getOrNull(0) ?: "Dada"}' gewählt habt."
                    it[mission2] = "Diskutiert die kulturelle Relevanz von '${topAnswers.getOrNull(1) ?: "Nichts"}'."
                    it[mission3] = "Erfindet einen Schlachtruf, der eure gemeinsame Liebe zu ${topAnswers.getOrNull(0) ?: "Dada"} ausdrückt."
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

            val bestTeam = candidateTeams
                .filter { it.second.size < 6 }
                .maxByOrNull { (_, members) ->
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
        val t1Idx = Random.nextInt(newState.size)
        val t2Idx = Random.nextInt(newState.size)
        if (t1Idx == t2Idx) return newState
        val team1 = newState[t1Idx]; val team2 = newState[t2Idx]
        if (team1.isNotEmpty() && team2.isNotEmpty()) {
            val u1Idx = Random.nextInt(team1.size); val u2Idx = Random.nextInt(team2.size)
            val u1 = team1[u1Idx]; team1[u1Idx] = team2[u2Idx]; team2[u2Idx] = u1
        }
        return newState
    }
}

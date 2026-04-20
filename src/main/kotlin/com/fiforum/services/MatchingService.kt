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
            // Anti-Clique Rule: Penalize if a user has 0 overlaps with anyone else in the team
            if (!userHasAnyOverlap && members.size > 1) {
                total -= 50
            }
        }
        // Since we iterate i and j, we counted each pair twice. Divide by 2 for pair scores, 
        // but the anti-clique penalty is per user, so it's already "correctly" weighted relative to pairs.
        return total / 2 
    }

    fun runBatchMatching() {
        if (isLaunched) return

        transaction {
            val allUsers = Users.selectAll().map {
                UserData(it[Users.email], it[Users.name], it[Users.company], it[Users.hobby], it[Users.techInterest], it[Users.travel], it[Users.workstyle], it[Users.coffeeTalk], it[Users.afterWork], it[Users.popculture], it[Users.fuel])
            }

            if (allUsers.isEmpty()) return@transaction

            // --- GLOBAL OPTIMIZATION ---
            var bestState = initialGrouping(allUsers)
            var bestScore = calculateGlobalScore(bestState)

            repeat(10) { // 10 Random Restarts
                var currentState = initialGrouping(allUsers)
                var currentScore = calculateGlobalScore(currentState)
                
                repeat(5000) { // 5000 Iterations
                    val nextState = mutateState(currentState)
                    val nextScore = calculateGlobalScore(nextState)
                    if (nextScore > currentScore) {
                        currentState = nextState
                        currentScore = nextScore
                    }
                }
                
                if (currentScore > bestScore) {
                    bestState = currentState
                    bestScore = currentScore
                }
            }

            // Save results
            bestState.forEach { members ->
                val interests = members.flatMap { listOf(it.hobby, it.techInterest) }.filter { it.isNotBlank() }
                val frequencies = interests.groupingBy { it }.eachCount()
                val topInterests = frequencies.entries.sortedByDescending { it.value }.take(2).map { it.key }
                
                val teamName = when {
                    topInterests.size >= 2 -> "Team ${topInterests[0]} & ${topInterests[1]}"
                    topInterests.size == 1 -> "Team ${topInterests[0]}"
                    else -> "Die Allrounder"
                }

                val tId = TeamsTable.insert {
                    it[name] = teamName
                }[TeamsTable.id]

                members.forEach { m -> Users.update({ Users.email eq m.email }) { it[teamId] = tId } }
            }
            isLaunched = true
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

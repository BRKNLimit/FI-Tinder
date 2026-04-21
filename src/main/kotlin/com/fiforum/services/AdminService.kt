package com.fiforum.services

import com.fiforum.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object AdminService {

    fun resetAll() {
        transaction {
            TeamsTable.deleteAll()
            Users.update {
                it[teamId] = null
                it[q1] = null; it[q2] = null; it[q3] = null; it[q4] = null; it[q5] = null
                it[q6] = null; it[q7] = null; it[q8] = null; it[q9] = null; it[q10] = null
            }
            MatchingService.isLaunched = false
        }
    }

    fun getStats(): Map<String, Any> {
        return transaction {
            val totalUsers = Users.selectAll().count()
            val totalTeams = TeamsTable.selectAll().count()
            val surveyCompleted = Users.selectAll().where { Users.q1.isNotNull() }.count()
            
            val companyDist = Users.selectAll()
                .map { it[Users.company] }
                .groupingBy { it }
                .eachCount()

            mapOf(
                "totalUsers" to totalUsers,
                "totalTeams" to totalTeams,
                "surveyCompleted" to surveyCompleted,
                "companyDist" to companyDist
            )
        }
    }
}

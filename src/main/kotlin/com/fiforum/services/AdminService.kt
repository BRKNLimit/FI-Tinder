package com.fiforum.services

import com.fiforum.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import kotlin.random.Random

object AdminService {

    fun resetAll() {
        transaction {
            TeamsTable.deleteAll()
            Users.deleteAll() // Full reset including users
            MatchingService.isLaunched = false
        }
    }

    fun generateMockData(count: Int = 20) {
        val companies = listOf("Finanz Informatik", "FI-TS", "FI-SP", "Star Finanz", "inasys", "FINMAS")
        val names = listOf("Lars", "Marie", "Nils", "Svenja", "Christian", "Julia", "Thomas", "Sarah", "Michael", "Laura")
        val batchId = Random.nextInt(1000, 9999)
        
        val questions = listOf(
            listOf("Harambes Tod", "Timmys Strandung"),
            listOf("Rot", "Blau"),
            listOf("Saturn", "Uranus"),
            listOf("Auf dem Mars", "Auf dem Mond"),
            listOf("Am Strand", "Unter Wasser"),
            listOf("Ein Jedi", "Ein Sith"),
            listOf("10 cm groß", "10 Meter groß"),
            listOf("Eine Palme", "Eine Eiche"),
            listOf("Girokonto", "Kreditkarte"),
            listOf("Fahrrad", "Pedalo")
        )

        transaction {
            repeat(count) { i ->
                val email = "test_${batchId}_$i@example.com"
                Users.insert {
                    it[Users.email] = email
                    it[passwordHash] = BCrypt.hashpw("password", BCrypt.gensalt())
                    it[name] = "${names.random()} #$batchId-$i"
                    it[company] = companies.random()
                    it[q1] = questions[0].random()
                    it[q2] = questions[1].random()
                    it[q3] = questions[2].random()
                    it[q4] = questions[3].random()
                    it[q5] = questions[4].random()
                    it[q6] = questions[5].random()
                    it[q7] = questions[6].random()
                    it[q8] = questions[7].random()
                    it[q9] = questions[8].random()
                    it[q10] = questions[9].random()
                }
            }
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

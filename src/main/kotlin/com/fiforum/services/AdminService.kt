package com.fiforum.services

import com.fiforum.models.TeamsTable
import com.fiforum.models.Users
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random

object AdminService {

    private val names = listOf("Lars", "Anna", "Ben", "Sophie", "Max", "Julia", "Tom", "Emma", "Felix", "Lena", "Moritz", "Klara", "Paul", "Marie", "Jonas", "Sarah", "Tim", "Lisa", "Niklas", "Laura")
    private val companies = listOf("Finanz Informatik", "FI-TS", "FI-SP", "Star Finanz", "inasys", "FINMAS")
    private val hobbies = listOf("Fußball", "Kochen", "Gaming", "Wandern", "Lesen", "Reisen", "Fotografie", "Musik", "Yoga", "Malen")
    private val techInterests = listOf("Kotlin", "AI", "Cloud", "Cyber Security", "Blockchain", "DevOps", "Frontend", "Backend", "Mobile", "Data Science")
    private val travelOptions = listOf("Asien", "Nordamerika", "Australien", "Afrika", "Skandinavien", "Südamerika", "Hauptsache Warm", "Hauptsache Action", "Süd Europa")
    private val coffeeTopics = listOf("Tech-Gossip", "Börse & Krypto", "Sport-Ergebnisse", "Filme & Serien", "Haustiere & Alltag", "Urlaubspläne", "Gaming News", "Lokale Events")
    private val workstyleOptions = listOf("... Remote", "... im Office", "... möglichst früh", "... möglichst spät")
    private val afterWorkOptions = listOf("Feierabend Bier", "Ab zum Sport", "Ab auf die Couch", "Side Hustle", "Fancy Kochen")
    private val options = listOf("Ja", "Nein", "Manchmal", "Vielleicht")

    fun generateMockData(count: Int) {
        transaction {
            repeat(count) {
                val mail = "user${Random.nextInt(100000)}@example.com"
                Users.insert {
                    it[email] = mail
                    it[name] = names.random()
                    it[company] = companies.random()
                    it[hobby] = hobbies.random()
                    it[techInterest] = techInterests.random()
                    it[travel] = travelOptions.random()
                    it[workstyle] = workstyleOptions.random()
                    it[coffeeTalk] = coffeeTopics.random()
                    it[afterWork] = afterWorkOptions.random()
                    it[popculture] = options.random()
                    it[fuel] = options.random()
                }
            }
        }
    }

    fun resetAll() {
        transaction {
            Users.deleteAll()
            TeamsTable.deleteAll()
            MatchingService.isLaunched = false
        }
    }
}

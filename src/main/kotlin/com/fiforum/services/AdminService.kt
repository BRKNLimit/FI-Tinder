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
    private val hobbies = listOf("Fußball", "Wandern", "Kochen", "Gaming", "Lesen", "Reisen", "Fotografie", "Musik", "Yoga", "Malen", "Gym / Fitness", "Teamsport")
    private val techInterests = listOf("Programmieren", "AI", "Cloud", "Cyber Security", "BlockChain", "Devops", "Data Science", "FinTech", "Agile/Scrum", "Business Intelligence", "UX/UI Design", "Projektmanagement", "E-Commerce")
    private val travelOptions = listOf("Asien", "Nordamerika", "Südamerika", "Südeuropa", "Skandinavien", "Hauptsache warm", "Hauptsache Action", "Australien", "Afrika", "Städtetrip", "Roadtrip", "Balkonien")
    private val coffeeTopics = listOf("Filme und Serien", "Tech Gossip", "Krypto und Finanzen", "Sportergebnisse", "Haustier und Alltag", "Urlaubspläne", "Gaming News", "Lokale Events", "Studium & Berufsschule")
    private val workstyleOptions = listOf("Remote", "im Office", "Hybrid", "möglichst früh", "möglichst spät")
    private val afterWorkOptions = listOf("Feierabendbier", "ab zum Sport", "ab auf die Couch", "Side Hustle", "Fancy kochen", "Freunde treffen", "Zocken")
    private val fuelOptions = listOf("Kaffee", "Energy Drinks", "Mate", "Spezi / Cola", "Tee", "Wasser (stay hydrated)", "Snacks")
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
                    it[fuel] = fuelOptions.random()
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

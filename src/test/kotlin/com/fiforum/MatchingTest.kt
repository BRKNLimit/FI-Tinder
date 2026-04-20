package com.fiforum

import com.fiforum.models.UserData
import com.fiforum.services.MatchingService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan

class MatchingTest : StringSpec({

    "identical users with same company should have negative score due to massive penalty" {
        val u1 = UserData("lars@example.com", "Lars", "Finanz Informatik", "Gaming", "Kotlin", "Asien", "... Remote", "Tech-Gossip", "Ab zum Sport", "Ja", "Kaffee")
        val u2 = UserData("tester@example.com", "Tester", "Finanz Informatik", "Gaming", "Kotlin", "Asien", "... Remote", "Tech-Gossip", "Ab zum Sport", "Ja", "Kaffee")
        
        val score = MatchingService.calculatePairScore(u1, u2)
        println("Score for identical users same company: $score")
        // New Weights: 10+10+5+5+5+5+5+5 = 50. Company penalty: -100. Total: -50.
        score shouldBeLessThan 0
    }

    "identical users with different company should have high positive score" {
        val u1 = UserData("lars@example.com", "Lars", "Finanz Informatik", "Gaming", "Kotlin", "Asien", "... Remote", "Tech-Gossip", "Ab zum Sport", "Ja", "Kaffee")
        val u2 = UserData("tester@example.com", "Tester", "Star Finanz", "Gaming", "Kotlin", "Asien", "... Remote", "Tech-Gossip", "Ab zum Sport", "Ja", "Kaffee")
        
        val score = MatchingService.calculatePairScore(u1, u2)
        println("Score for identical users different company: $score")
        // Total max synergy: 50.
        score shouldBeGreaterThan 40
    }
})

package com.fiforum

import com.fiforum.models.UserData
import com.fiforum.services.MatchingService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan

class MatchingTest : StringSpec({

    "identical users with same company should have moderated score" {
        val u1 = UserData("lars@example.com", "Lars", "Finanz Informatik", "Gaming", "Kotlin", "Asien", "... Remote", "Tech-Gossip", "Ab zum Sport", "", "Kaffee")
        val u2 = UserData("tester@example.com", "Tester", "Finanz Informatik", "Gaming", "Kotlin", "Asien", "... Remote", "Tech-Gossip", "Ab zum Sport", "", "Kaffee")
        
        val score = MatchingService.calculatePairScore(u1, u2)
        println("Score for identical users same company: $score")
        // New Weights: 20+20+10+10+10+10+10 = 90. Company penalty: -25. Total: 65.
        score shouldBeGreaterThan 0
    }

    "identical users with different company should have high positive score" {
        val u1 = UserData("lars@example.com", "Lars", "Finanz Informatik", "Gaming", "Kotlin", "Asien", "... Remote", "Tech-Gossip", "Ab zum Sport", "", "Kaffee")
        val u2 = UserData("tester@example.com", "Tester", "Star Finanz", "Gaming", "Kotlin", "Asien", "... Remote", "Tech-Gossip", "Ab zum Sport", "", "Kaffee")
        
        val score = MatchingService.calculatePairScore(u1, u2)
        println("Score for identical users different company: $score")
        score shouldBeGreaterThan 40
    }
})

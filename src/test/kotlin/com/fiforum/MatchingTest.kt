package com.fiforum

import com.fiforum.models.UserData
import com.fiforum.services.MatchingService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan

class MatchingTest : StringSpec({
    "users from same company should have negative score" {
        val u1 = UserData("u1@test.com", "User 1", "Company A", q1 = "A")
        val u2 = UserData("u2@test.com", "User 2", "Company A", q1 = "A")
        
        val score = MatchingService.calculatePairScore(u1, u2)
        println("Score for same company: $score")
        score shouldBeLessThan 0
    }

    "identical users with different company should have high positive score" {
        val u1 = UserData("u1@test.com", "User 1", "Company A", 
            q1="A", q2="A", q3="A", q4="A", q5="A", q6="A", q7="A", q8="A", q9="A", q10="A")
        val u2 = UserData("u2@test.com", "User 2", "Company B", 
            q1="A", q2="A", q3="A", q4="A", q5="A", q6="A", q7="A", q8="A", q9="A", q10="A")
        
        val score = MatchingService.calculatePairScore(u1, u2)
        println("Score for identical users different company: $score")
        // Total max synergy: 10.
        score shouldBeGreaterThan 9
    }
})

package com.fiforum.services

import com.fiforum.models.UserData
import com.fiforum.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object BadgeService {

    fun calculateBadges(user: UserData, teamMembers: List<UserData>, allEmailsSortedByJoin: List<String>, totalUserCount: Long): List<String> {
        val badges = mutableListOf<String>()

        // schauen wer am schnelsten war für die alpha/beta badges
        if (user.isLatecomer) {
            badges.add("latecomer")
        } else {
            val rank = allEmailsSortedByJoin.indexOf(user.email) + 1
            if (totalUserCount > 0) {
                when {
                    rank <= totalUserCount * 0.1 -> badges.add("alpha_10")
                    rank <= totalUserCount * 0.5 -> badges.add("beta_50")
                    rank > totalUserCount * 0.9 -> badges.add("gamma_10")
                    else -> badges.add("active_member")
                }
            }
        }

        if (user.q1 == "Harambes Tod") badges.add("justice_for_harambe")
        if (user.q1 == "Timmys Strandung") badges.add("free_timmy")
        if (user.q4 == "Auf dem Mars" && user.q9 == "Girokonto") badges.add("galactic_finance")
        
        val userAnswers = listOfNotNull(user.q1, user.q2, user.q3, user.q4, user.q5, user.q6, user.q7, user.q8, user.q9, user.q10)
        if (userAnswers.size == 10) {
            val aOptions = setOf("Harambes Tod", "Rot", "Saturn", "Auf dem Mars", "Am Strand", "Ein Jedi", "10 cm groß", "Eine Palme", "Girokonto", "Dienstag")
            val aCount = userAnswers.count { it in aOptions }
            // genau halbe-halbe gemacht, der typ is voll neutral
            if (aCount == 5) badges.add("true_neutral")
        }
        
        if (user.q10 == "Dienstag" || user.q10 == "Donnerstag") badges.add("synesthesia")

        if (user.q4 == "Auf dem Mars" && user.q9 == "Kreditkarte") badges.add("mars_makler")
        if (user.q1 == "Timmys Strandung" && user.q5 == "Unter Wasser") badges.add("timmy_rescue")
        if (user.q7 == "10 cm groß" && user.q6 == "Ein Jedi") badges.add("bonsai_jedi")
        if (user.q10 == "Dienstag" && user.q2 == "Rot") badges.add("tuesday_diva")
        if (user.q8 == "Eine Eiche" && user.q9 == "Girokonto") badges.add("oak_investor")
        if (user.q3 == "Uranus" && user.q5 == "Unter Wasser") badges.add("uranus_hermit")
        if (user.q1 == "Harambes Tod" && user.q7 == "10 Meter groß") badges.add("harambe_legacy")
        if (user.q10 == "Donnerstag" && user.q8 == "Eine Palme") badges.add("thursday_gourmet")

        if (teamMembers.size == 5) badges.add("full_house")
        
        val companies = teamMembers.map { it.company }.distinct()
        if (companies.size == teamMembers.size && teamMembers.size > 1) badges.add("diversity_pro")

        if (teamMembers.isNotEmpty()) {
            val firstQ10 = teamMembers.first().q10
            // falls alle die gleice antwort bei q10 haben gibts den hive mind
            if (firstQ10 != null && teamMembers.all { it.q10 == firstQ10 } && teamMembers.size > 1) {
                badges.add("hive_mind_dada")
            }
        }

        var maxShared = 0
        teamMembers.forEach { m ->
            if (m.email != user.email) {
                val otherAns = listOfNotNull(m.q1, m.q2, m.q3, m.q4, m.q5, m.q6, m.q7, m.q8, m.q9, m.q10)
                val count = userAnswers.intersect(otherAns.toSet()).size
                if (count > maxShared) maxShared = count
            }
        }
        if (maxShared > 4) badges.add("synergy_master")

        // mehr als zehen badges passen eh nich auf die karte sons siehts kacke aus
        return badges.distinct().take(10)
    }
}

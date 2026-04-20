package com.fiforum.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : Table("users") {
    val email = varchar("email", 100)
    val passwordHash = varchar("passwordHash", 100)
    val name = varchar("name", 100).default("")
    val company = varchar("company", 100).default("")
    val hobby = varchar("hobby", 100).default("")
    val techInterest = varchar("techInterest", 100).default("")
    val travel = varchar("travel", 100).default("")
    val workstyle = varchar("workstyle", 100).default("")
    val coffeeTalk = varchar("coffeeTalk", 100).default("")
    val afterWork = varchar("afterWork", 100).default("")
    val popculture = varchar("popculture", 100).default("")
    val fuel = varchar("fuel", 100).default("")
    val teamId = integer("team_id").nullable()
    
    // Networking & Profile fields
    val linkedinUrl = varchar("linkedinUrl", 255).nullable()
    val xingUrl = varchar("xingUrl", 255).nullable()
    val profilePicture = text("profilePicture").nullable() // Base64 storage

    // New personal data fields
    val phonePrivate = varchar("phonePrivate", 50).nullable()
    val phoneWork = varchar("phoneWork", 50).nullable()
    val address = varchar("address", 255).nullable()
    val zipCode = varchar("zipCode", 20).nullable()
    
    // Position/Timestamp field
    val joinedAt = datetime("joinedAt").default(LocalDateTime.now())
    
    override val primaryKey = PrimaryKey(email)
}

object TeamsTable : Table("teams") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 150)
    val mission1 = text("mission1").nullable()
    val mission2 = text("mission2").nullable()
    val mission3 = text("mission3").nullable()
    val currentMissionIndex = integer("currentMissionIndex").default(1)
    val teamColor = varchar("teamColor", 20).default("#ff0000") // Red fallback
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class UserData(
    val email: String,
    val name: String,
    val company: String,
    val hobby: String,
    val techInterest: String,
    val travel: String,
    val workstyle: String,
    val coffeeTalk: String,
    val afterWork: String,
    val popculture: String,
    val fuel: String,
    val linkedinUrl: String? = null,
    val xingUrl: String? = null,
    val profilePicture: String? = null,
    val phonePrivate: String? = null,
    val phoneWork: String? = null,
    val address: String? = null,
    val zipCode: String? = null,
    val joinedAt: String? = null
)

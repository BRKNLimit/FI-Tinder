package com.fiforum.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : Table("users") {
    val email = varchar("email", 100)
    val passwordHash = varchar("passwordHash", 100)
    val name = varchar("name", 100).default("")
    val company = varchar("company", 100).default("")
    
    // 10 Dadaist Questions
    val q1 = varchar("q1", 50).nullable()
    val q2 = varchar("q2", 50).nullable()
    val q3 = varchar("q3", 50).nullable()
    val q4 = varchar("q4", 50).nullable()
    val q5 = varchar("q5", 50).nullable()
    val q6 = varchar("q6", 50).nullable()
    val q7 = varchar("q7", 50).nullable()
    val q8 = varchar("q8", 50).nullable()
    val q9 = varchar("q9", 50).nullable()
    val q10 = varchar("q10", 50).nullable()

    val teamId = integer("team_id").nullable()
    
    // Networking & Profile fields (Keep for VCard/ID)
    val linkedinUrl = varchar("linkedinUrl", 255).nullable()
    val xingUrl = varchar("xingUrl", 255).nullable()
    val profilePicture = text("profilePicture").nullable()

    val phonePrivate = varchar("phonePrivate", 50).nullable()
    val phoneWork = varchar("phoneWork", 50).nullable()
    val address = varchar("address", 255).nullable()
    val zipCode = varchar("zipCode", 20).nullable()
    
    val joinedAt = datetime("joinedAt").default(LocalDateTime.now())

    val hasDownloadedVCard = bool("hasDownloadedVCard").default(false)
    val allowVCardDownload = bool("allowVCardDownload").default(true)
    val isLatecomer = bool("isLatecomer").default(false)
    
    override val primaryKey = PrimaryKey(email)
}

@Serializable
data class UserData(
    val email: String,
    val name: String,
    val company: String,
    val q1: String? = null,
    val q2: String? = null,
    val q3: String? = null,
    val q4: String? = null,
    val q5: String? = null,
    val q6: String? = null,
    val q7: String? = null,
    val q8: String? = null,
    val q9: String? = null,
    val q10: String? = null,
    val teamId: Int? = null,
    val linkedinUrl: String? = null,
    val xingUrl: String? = null,
    val profilePicture: String? = null,
    val phonePrivate: String? = null,
    val phoneWork: String? = null,
    val address: String? = null,
    val zipCode: String? = null,
    val joinedAt: String? = null,
    val hasDownloadedVCard: Boolean = false,
    val allowVCardDownload: Boolean = true,
    val isLatecomer: Boolean = false,
    var joinBadge: String? = null
)

fun ResultRow.toUserData() = UserData(
    email = this[Users.email],
    name = this[Users.name],
    company = this[Users.company],
    q1 = this[Users.q1],
    q2 = this[Users.q2],
    q3 = this[Users.q3],
    q4 = this[Users.q4],
    q5 = this[Users.q5],
    q6 = this[Users.q6],
    q7 = this[Users.q7],
    q8 = this[Users.q8],
    q9 = this[Users.q9],
    q10 = this[Users.q10],
    teamId = this[Users.teamId],
    linkedinUrl = this[Users.linkedinUrl],
    xingUrl = this[Users.xingUrl],
    profilePicture = this[Users.profilePicture],
    phonePrivate = this[Users.phonePrivate],
    phoneWork = this[Users.phoneWork],
    address = this[Users.address],
    zipCode = this[Users.zipCode],
    joinedAt = this[Users.joinedAt].toString(),
    hasDownloadedVCard = this[Users.hasDownloadedVCard],
    allowVCardDownload = this[Users.allowVCardDownload],
    isLatecomer = this[Users.isLatecomer]
)

object TeamsTable : Table("teams") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val mission1 = text("mission1").nullable()
    val mission2 = text("mission2").nullable()
    val mission3 = text("mission3").nullable()
    val currentMissionIndex = integer("currentMissionIndex").default(1)
    val teamColor = varchar("teamColor", 20).default("#ff0000")
    val lastTeamFindClick = datetime("lastTeamFindClick").nullable()
    override val primaryKey = PrimaryKey(id)
}

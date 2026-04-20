package com.fiforum.models

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val email = varchar("email", 100)
    val name = varchar("name", 100)
    val company = varchar("company", 100)
    val hobby = varchar("hobby", 100)
    val techInterest = varchar("techInterest", 100)
    val travel = varchar("travel", 100)
    val workstyle = varchar("workstyle", 100)
    val coffeeTalk = varchar("coffeeTalk", 100)
    val afterWork = varchar("afterWork", 100)
    val popculture = varchar("popculture", 100)
    val fuel = varchar("fuel", 100)
    val teamId = integer("team_id").nullable()
    override val primaryKey = PrimaryKey(email)
}

object TeamsTable : Table("teams") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 150)
    override val primaryKey = PrimaryKey(id)
}

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
    val fuel: String
)

package com.fiforum

import com.fiforum.models.TeamsTable
import com.fiforum.models.Users
import com.fiforum.routes.configureRouting
import com.fiforum.services.MatchingService
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    Database.connect("jdbc:sqlite:./matchmaker.db", "org.sqlite.JDBC")
    transaction { SchemaUtils.create(Users, TeamsTable) }

    transaction {
        val assignedUsersCount = Users.select { Users.teamId.isNotNull() }.count()
        if (assignedUsersCount > 0) MatchingService.isLaunched = true
    }

    configureRouting()
}

package com.fiforum

import com.fiforum.models.TeamsTable
import com.fiforum.models.Users
import com.fiforum.routes.configureRouting
import com.fiforum.services.MatchingService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.websocket.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    install(ContentNegotiation) {
        json()
    }

    Database.connect("jdbc:sqlite:./matchmaker.db", "org.sqlite.JDBC")
    transaction { SchemaUtils.createMissingTablesAndColumns(Users, TeamsTable) }

    transaction {
        val assignedUsersCount = Users.select { Users.teamId.isNotNull() }.count()
        if (assignedUsersCount > 0) MatchingService.isLaunched = true
    }

    configureRouting()
}

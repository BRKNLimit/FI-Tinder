package com.fiforum.routes

import com.fiforum.services.MatchingSocketService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Application.configureRouting() {
    routing {
        mainRoutes()
        userRoutes()
        adminRoutes()
        
        webSocket("/matching-ws") {
            MatchingSocketService.addSession(this)
            try {
                for (frame in incoming) {
                    // Just keep connection alive
                }
            } finally {
                MatchingSocketService.removeSession(this)
            }
        }
    }
}

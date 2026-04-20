package com.fiforum.services

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object MatchingSocketService {
    private val sessions = Collections.newSetFromMap(ConcurrentHashMap<DefaultWebSocketServerSession, Boolean>())

    fun addSession(session: DefaultWebSocketServerSession) {
        sessions.add(session)
    }

    fun removeSession(session: DefaultWebSocketServerSession) {
        sessions.remove(session)
    }

    suspend fun broadcastMatchingFinished() {
        sessions.forEach { session ->
            try {
                session.send(Frame.Text("MATCHING_FINISHED"))
            } catch (e: Exception) {
                // Session might be closed
            }
        }
    }
}

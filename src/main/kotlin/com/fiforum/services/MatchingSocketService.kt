package com.fiforum.services

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object MatchingSocketService {
    private val sessions = Collections.newSetFromMap(ConcurrentHashMap<DefaultWebSocketServerSession, Boolean>())
    private val teamSessions = ConcurrentHashMap<Int, MutableSet<DefaultWebSocketServerSession>>()

    fun addSession(session: DefaultWebSocketServerSession, teamId: Int? = null) {
        sessions.add(session)
        if (teamId != null) {
            teamSessions.computeIfAbsent(teamId) { Collections.newSetFromMap(ConcurrentHashMap()) }.add(session)
        }
    }

    fun removeSession(session: DefaultWebSocketServerSession) {
        sessions.remove(session)
        teamSessions.values.forEach { it.remove(session) }
    }

    suspend fun broadcastMatchingFinished() {
        sessions.forEach { session ->
            try {
                session.send(Frame.Text("MATCHING_FINISHED"))
            } catch (e: Exception) {}
        }
    }

    suspend fun broadcastMatchingStarted() {
        sessions.forEach { session ->
            try {
                session.send(Frame.Text("MATCHING_STARTED"))
            } catch (e: Exception) {}
        }
    }

    suspend fun broadcastNewMission() {
        sessions.forEach { session ->
            try {
                session.send(Frame.Text("NEW_MISSION"))
            } catch (e: Exception) {}
        }
    }

    suspend fun broadcastTeamFind(teamId: Int) {
        teamSessions[teamId]?.forEach { session ->
            try {
                session.send(Frame.Text("TEAM_FINDEN"))
            } catch (e: Exception) {}
        }
    }
}

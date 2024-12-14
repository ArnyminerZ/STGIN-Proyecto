package com.arnyminerz.upv.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val id: Int)

fun Application.installSessions() {
    install(Sessions) {
        cookie<UserSession>("user_session")
    }
}

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
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 60 * 24 * 30 // 30 days
            cookie.httpOnly = true
            cookie.secure = true
        }
    }
}

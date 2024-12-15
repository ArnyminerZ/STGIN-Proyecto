package com.arnyminerz.upv.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.util.hex
import java.time.Instant
import kotlinx.serialization.Serializable

private const val SessionDuration: Long = 30 * 24 * 60 * 60

@Serializable
data class UserSession(
    val id: Int,
    val username: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun expiresAt(): Instant = Instant.ofEpochMilli(createdAt + SessionDuration * 1000)

    fun isExpired(): Boolean = expiresAt() < Instant.now()
}

fun Application.installSessions() {
    val secretEncryptKey = hex("00112233445566778899aabbccddeeff")
    val secretSignKey = hex("6819b57a326945c1968f45236589")

    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = SessionDuration // 30 days
            cookie.httpOnly = true
            cookie.secure = true
            cookie.extensions["SameSite"] = "strict"

            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }
}

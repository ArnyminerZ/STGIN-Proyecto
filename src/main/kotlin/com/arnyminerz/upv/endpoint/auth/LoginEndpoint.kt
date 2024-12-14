package com.arnyminerz.upv.endpoint.auth

import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.User
import com.arnyminerz.upv.database.entity.UserSession
import com.arnyminerz.upv.endpoint.type.Endpoint
import com.arnyminerz.upv.error.Errors
import com.arnyminerz.upv.plugins.UserSession as UserSessionData
import com.arnyminerz.upv.security.Passwords
import io.ktor.http.HttpMethod
import io.ktor.server.plugins.origin
import io.ktor.server.request.userAgent
import io.ktor.server.routing.RoutingContext
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Used for authenticating as a user.
 * Sessions are handled by ktor using cookies.
 */
object LoginEndpoint : Endpoint("/api/auth/login", HttpMethod.Post) {
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun RoutingContext.body() {
        val (username, password) = basicCredentials()

        val user = ServerDatabase { User.findById(username) }
        if (user == null) {
            respondFailure(Errors.InvalidCredentials)
        }
        val (hash, salt) = user!!.hash to user.salt
        if (!Passwords.verify(salt, hash, password)) {
            respondFailure(Errors.InvalidCredentials)
        }

        val sessionId = ServerDatabase {
            UserSession.new {
                this.ip = call.request.origin.remoteHost
                this.userAgent = call.request.userAgent()

                this.user = user
            }.id.value
        }

        call.sessions.set(UserSessionData(sessionId))

        respondSuccess()
    }
}

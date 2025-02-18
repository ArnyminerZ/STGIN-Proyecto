package com.arnyminerz.upv.endpoint.auth

import Endpoints
import com.arnyminerz.upv.cache.CacheRepository
import com.arnyminerz.upv.cache.UserSession
import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.User
import com.arnyminerz.upv.endpoint.type.Endpoint
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.error.Errors
import com.arnyminerz.upv.security.Passwords
import io.ktor.http.HttpMethod
import io.ktor.server.plugins.origin
import io.ktor.server.request.userAgent
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import com.arnyminerz.upv.plugins.UserSession as UserSessionData

/**
 * Used for authenticating as a user.
 * Sessions are handled by ktor using cookies.
 */
object LoginEndpoint : Endpoint(Endpoints.Auth.LOGIN, HttpMethod.Post) {
    @OptIn(ExperimentalEncodingApi::class, ExperimentalUuidApi::class)
    override suspend fun EndpointContext.body() {
        val (username, password) = fetchCredentials()

        val user = ServerDatabase { User.findById(username) }
        if (user == null) {
            // This is a bad pattern since allows user scrapping, but we use it for the sake of simplicity
            respondFailure(Errors.UserNotFound)
        }
        val (hash, salt) = user!!.hash to user.salt
        if (!Passwords.verify(salt, hash, password)) {
            respondFailure(Errors.WrongPassword)
        }

        val sessionId = Uuid.random()
        val session = UserSessionData(sessionId, username)

        call.sessions.set(session)

        CacheRepository.setSession(
            UserSession.new(
                expiresAt = session.expiresAt(),
                ip = call.request.origin.remoteHost,
                userAgent = call.request.userAgent(),
                userId = user.id.value,
                uuid = sessionId
            )
        )

        respondSuccess()
    }
}

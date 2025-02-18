package com.arnyminerz.upv.endpoint.type

import com.arnyminerz.upv.cache.CacheRepository
import com.arnyminerz.upv.error.Errors
import com.arnyminerz.upv.plugins.UserSession
import io.ktor.http.HttpMethod
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlin.uuid.ExperimentalUuidApi

abstract class SecureEndpoint(route: String, method: HttpMethod = HttpMethod.Post): Endpoint(route, method) {
    @OptIn(ExperimentalUuidApi::class)
    final override suspend fun EndpointContext.body() {
        // Get the session
        val session = call.sessions.get<UserSession>()

        // If it's null, the user is not logged in
        if (session == null || session.isExpired()) {
            respondFailure(Errors.NotLoggedIn)
            return
        }

        // If it's not null, the user is logged in, but the session may no longer be valid, so check on the database
        val userSession = CacheRepository.getSessionByUUID(session.uuid)
        if (userSession == null) {
            respondFailure(Errors.NotLoggedIn)
            return
        }

        // Update the last seen
        CacheRepository.updateLastSeen(userSession)

        secureBody(userSession.userId)
    }

    protected abstract suspend fun EndpointContext.secureBody(userId: String)
}

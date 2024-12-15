package com.arnyminerz.upv.endpoint.type

import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.User
import com.arnyminerz.upv.error.Errors
import com.arnyminerz.upv.plugins.UserSession
import io.ktor.http.HttpMethod
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import java.time.Instant

abstract class SecureEndpoint(route: String, method: HttpMethod = HttpMethod.Post): Endpoint(route, method) {
    final override suspend fun EndpointContext.body() {
        // Get the session
        val session = call.sessions.get<UserSession>()

        // If it's null, the user is not logged in
        if (session == null || session.isExpired()) {
            respondFailure(Errors.NotLoggedIn)
        }

        // If it's not null, the user is logged in, but the session may no longer be valid, so check on the database
        val userSession = ServerDatabase { com.arnyminerz.upv.database.entity.UserSession.findById(session!!.id) }
        if (userSession == null) {
            respondFailure(Errors.NotLoggedIn)
        }

        // Update the last seen
        ServerDatabase { userSession!!.lastSeen = Instant.now() }

        val user = ServerDatabase { userSession!!.user }
        secureBody(user)
    }

    protected abstract suspend fun EndpointContext.secureBody(user: User)
}

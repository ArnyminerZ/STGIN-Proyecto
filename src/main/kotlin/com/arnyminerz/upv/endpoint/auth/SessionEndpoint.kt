package com.arnyminerz.upv.endpoint.auth

import com.arnyminerz.upv.database.entity.UserSession as UserSessionEntity
import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.endpoint.type.Endpoint
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.plugins.UserSession
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import java.time.Instant

/**
 * Checks whether the user is logged in or not.
 */
object SessionEndpoint : Endpoint("/api/auth/session", HttpMethod.Get) {
    override suspend fun EndpointContext.body() {
        // Get the session
        val session = call.sessions.get<UserSession>()

        // If it's null, the user is not logged in
        if (session == null || session.isExpired()) {
            respondSuccess("NO", HttpStatusCode.Unauthorized)
        }

        // If it's not null, the user is logged in, but the session may no longer be valid, so check on the database
        val userSession = ServerDatabase { UserSessionEntity.findById(session!!.id) }
        if (userSession == null) {
            respondSuccess("NO", HttpStatusCode.Unauthorized)
        }

        // Update the last seen
        ServerDatabase { userSession?.lastSeen = Instant.now() }

        respondSuccess(session!!.username, HttpStatusCode.OK)
    }
}

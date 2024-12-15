package com.arnyminerz.upv.endpoint.auth

import com.arnyminerz.upv.endpoint.type.Endpoint
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.plugins.UserSession
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.RoutingContext
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions

/**
 * Checks whether the user is logged in or not.
 */
object SessionEndpoint : Endpoint("/api/auth/session", HttpMethod.Get) {
    override suspend fun EndpointContext.body() {
        val session = call.sessions.get<UserSession>()

        if (session == null) {
            respondSuccess("NO", HttpStatusCode.NoContent)
        } else {
            respondSuccess("YES", HttpStatusCode.OK)
        }
    }
}

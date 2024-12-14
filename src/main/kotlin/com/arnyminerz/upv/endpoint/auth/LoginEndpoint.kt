package com.arnyminerz.upv.endpoint.auth

import com.arnyminerz.upv.endpoint.type.Endpoint
import io.ktor.http.HttpMethod
import io.ktor.server.routing.RoutingContext

/**
 * Used for authenticating as a user.
 * Sessions are handled by ktor using cookies.
 */
object LoginEndpoint : Endpoint("/api/auth/login", HttpMethod.Post) {
    override suspend fun RoutingContext.body() {
        TODO("Not yet implemented")
    }
}

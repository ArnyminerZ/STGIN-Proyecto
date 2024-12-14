package com.arnyminerz.upv.endpoint.auth

import com.arnyminerz.upv.endpoint.type.Endpoint
import io.ktor.http.HttpMethod
import io.ktor.server.routing.RoutingContext

/**
 * Registers a new user in the database.
 */
object RegisterEndpoint : Endpoint("/api/auth/register", HttpMethod.Post) {
    override suspend fun RoutingContext.body() {
        TODO("Not yet implemented")
    }
}

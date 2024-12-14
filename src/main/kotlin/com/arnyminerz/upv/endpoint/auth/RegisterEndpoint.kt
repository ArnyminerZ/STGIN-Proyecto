package com.arnyminerz.upv.endpoint.auth

import com.arnyminerz.upv.endpoint.type.Endpoint
import io.ktor.server.routing.RoutingContext

/**
 * Registers a new user in the database.
 */
object RegisterEndpoint : Endpoint("/register") {
    override suspend fun RoutingContext.body() {
        TODO("Not yet implemented")
    }
}

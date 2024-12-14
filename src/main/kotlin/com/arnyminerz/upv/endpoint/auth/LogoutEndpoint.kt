package com.arnyminerz.upv.endpoint.auth

import com.arnyminerz.upv.endpoint.type.Endpoint
import io.ktor.http.HttpMethod
import io.ktor.server.routing.RoutingContext

/**
 * Used for signing out. Invalidates the token of the user.
 */
object LogoutEndpoint : Endpoint("/api/auth/logout", HttpMethod.Post) {
    override suspend fun RoutingContext.body() {
        TODO("Not yet implemented")
    }
}

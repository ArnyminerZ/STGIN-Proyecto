package com.arnyminerz.upv.plugins

import com.arnyminerz.upv.endpoint.auth.LoginEndpoint
import com.arnyminerz.upv.endpoint.auth.LogoutEndpoint
import com.arnyminerz.upv.endpoint.auth.RegisterEndpoint
import com.arnyminerz.upv.endpoint.type.Endpoint
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

private val endpoints = listOf(
    // Authentication
    RegisterEndpoint,
    LoginEndpoint,
    LogoutEndpoint,
)

private fun Route.registerEndpoint(endpoint: Endpoint) {
    when (endpoint.method) {
        HttpMethod.Get -> get(endpoint.route, endpoint.handler)
        HttpMethod.Post -> post(endpoint.route, endpoint.handler)
        HttpMethod.Patch -> post(endpoint.route, endpoint.handler)
        HttpMethod.Delete -> post(endpoint.route, endpoint.handler)
        else -> error("Unsupported HTTP method ${endpoint.method}")
    }
}

fun Application.configureRouting() {
    routing {
        staticResources("/", "web")

        for (endpoint in endpoints) {
            registerEndpoint(endpoint)
        }
    }
}

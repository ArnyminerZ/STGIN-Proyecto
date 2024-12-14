package com.arnyminerz.upv.endpoint.type

import io.ktor.http.HttpMethod
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.RoutingHandler

abstract class Endpoint(val route: String, val method: HttpMethod = HttpMethod.Get) {
    val handler: RoutingHandler = { body() }

    abstract suspend fun RoutingContext.body()
}

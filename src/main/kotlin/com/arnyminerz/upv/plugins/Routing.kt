package com.arnyminerz.upv.plugins

import com.arnyminerz.upv.endpoint.auth.LoginEndpoint
import com.arnyminerz.upv.endpoint.auth.LogoutEndpoint
import com.arnyminerz.upv.endpoint.auth.RegisterEndpoint
import com.arnyminerz.upv.endpoint.auth.SessionEndpoint
import com.arnyminerz.upv.endpoint.game.BombEndpoint
import com.arnyminerz.upv.endpoint.game.GameEndpoint
import com.arnyminerz.upv.endpoint.game.MatchEndpoint
import com.arnyminerz.upv.endpoint.game.MatchReadyEndpoint
import com.arnyminerz.upv.endpoint.game.MatchesEndpoint
import com.arnyminerz.upv.endpoint.game.NewMatchEndpoint
import com.arnyminerz.upv.endpoint.game.PlaceBoatMatchEndpoint
import com.arnyminerz.upv.endpoint.game.StartMatchEndpoint
import com.arnyminerz.upv.endpoint.type.Endpoint
import com.arnyminerz.upv.endpoint.type.Websocket
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi

private val endpoints = listOf(
    // Authentication
    RegisterEndpoint,
    LoginEndpoint,
    LogoutEndpoint,
    SessionEndpoint,

    // Match endpoints
    MatchesEndpoint,
    MatchEndpoint,
    NewMatchEndpoint,
    StartMatchEndpoint,
    PlaceBoatMatchEndpoint,
    MatchReadyEndpoint,
    BombEndpoint,
)

@ExperimentalUuidApi
@ExperimentalEncodingApi
private val webSockets = listOf<Websocket>(
    GameEndpoint
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

@OptIn(ExperimentalEncodingApi::class, ExperimentalUuidApi::class)
fun Application.configureRouting() {
    routing {
        staticResources("/", "web")

        for (endpoint in endpoints) {
            registerEndpoint(endpoint)
        }
        for (webSocket in webSockets) {
            webSocket(path = webSocket.route, handler = webSocket.handler)
        }
    }
}

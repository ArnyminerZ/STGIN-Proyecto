package com.arnyminerz.upv.endpoint.type

import com.arnyminerz.upv.cache.CacheRepository
import com.arnyminerz.upv.endpoint.type.Endpoint.RequestHandledException
import com.arnyminerz.upv.plugins.UserSession
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.websocket.DefaultWebSocketServerSession
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi

@ExperimentalUuidApi
@ExperimentalEncodingApi
abstract class Websocket(val route: String) {
    val handler: suspend DefaultWebSocketServerSession.() -> Unit = {
        try {
            val context = WebsocketContext(this)
            context.handle()
        } catch (_: RequestHandledException) {
            // ignore error
        }
    }

    private suspend fun WebsocketContext.handle() {
        // Get the session
        val session = call.sessions.get<UserSession>()

        // If it's null, the user is not logged in
        if (session == null || session.isExpired()) {
            close("NOT_LOGGED_IN")
        }
        session!! // won't be null

        // If it's not null, the user is logged in, but the session may no longer be valid, so check on the database
        val userSession = CacheRepository.getSessionByUUID(session.uuid)
        if (userSession == null) {
            close("NOT_LOGGED_IN")
        }
        userSession!! // won't be null

        // Update the last seen
        CacheRepository.updateLastSeen(userSession)

        body(userSession.userId)
    }

    protected abstract suspend fun WebsocketContext.body(userId: String)
}

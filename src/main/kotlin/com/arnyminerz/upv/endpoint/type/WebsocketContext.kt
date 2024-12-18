package com.arnyminerz.upv.endpoint.type

import com.arnyminerz.upv.endpoint.type.Endpoint.RequestHandledException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import io.ktor.websocket.send
import kotlin.io.encoding.ExperimentalEncodingApi

@ExperimentalEncodingApi
class WebsocketContext(
    val session: WebSocketServerSession
) {
    val call: ApplicationCall get() = session.call

    /**
     * Closes the session with the given [code] as message and [CloseReason.Codes.CANNOT_ACCEPT] as code.
     * @throws RequestHandledException After closing the connection, for stopping the execution.
     */
    suspend fun close(code: String) {
        session.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, code))
        throw RequestHandledException("Request failed with error code $code")
    }

    suspend fun send(message: String) = session.send(message)

}

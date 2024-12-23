package com.arnyminerz.upv.endpoint.game

import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.endpoint.type.Websocket
import com.arnyminerz.upv.endpoint.type.WebsocketContext
import com.arnyminerz.upv.game.Orchestrator
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalEncodingApi::class, ExperimentalUuidApi::class)
object GameEndpoint : Websocket("/api/matches/{id}/socket") {
    override suspend fun WebsocketContext.body(userId: String) {
        val matchId = call.parameters["id"]?.toIntOrNull()
        if (matchId == null) {
            close("INVALID_MATCH_ID")
        }
        matchId!! // won't be null

        val match = ServerDatabase { Match.findById(matchId) }
        if (match == null) {
            close("MATCH_NOT_FOUND")
        }
        match!! // won't be null

        Orchestrator.actionsFlow.filter { it.matchId == matchId }.collect { action ->
            send(action.toString())
        }
    }
}

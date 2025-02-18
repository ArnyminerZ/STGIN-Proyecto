package com.arnyminerz.upv.endpoint.game

import Endpoints
import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.endpoint.game.StartMatchEndpoint.respondSuccess
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.error.Errors
import com.arnyminerz.upv.game.GameState
import io.ktor.http.HttpMethod

/**
 * Requests the server to start the match.
 */
object StartMatchEndpoint : MatchBaseEndpoint(Endpoints.Game.MATCH_START, HttpMethod.Post) {
    override suspend fun EndpointContext.matchBody(userId: String, match: Match) {
        if (match.startedAt != null) {
            respondFailure(Errors.MatchAlreadyStarted)
        }

        try {
            match.markReady(userId)
        } catch (_: IllegalStateException) {
            respondFailure(Errors.MatchNotReady)
        }

        if (match.isReady()) {
            match.start()

            respondSuccess()
        } else {
            respondSuccess()
        }
    }
}

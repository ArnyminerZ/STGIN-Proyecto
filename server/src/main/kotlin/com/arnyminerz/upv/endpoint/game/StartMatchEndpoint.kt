package com.arnyminerz.upv.endpoint.game

import Endpoints
import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.error.Errors
import io.ktor.http.HttpMethod

/**
 * Requests the server to start the match.
 */
object StartMatchEndpoint : MatchBaseEndpoint(Endpoints.Game.MATCH_START, HttpMethod.Post) {
    override suspend fun EndpointContext.matchBody(userId: String, match: Match) {
        if (!match.isReady()) {
            respondFailure(Errors.MatchNotReady)
        }
        if (match.startedAt != null) {
            respondFailure(Errors.MatchAlreadyStarted)
        }

        match.start()

        respondSuccess()
    }
}

package com.arnyminerz.upv.endpoint.game

import Endpoints
import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.endpoint.type.EndpointContext
import io.ktor.http.HttpMethod

/**
 * Accepts the match as the logged-in user.
 */
object AcceptMatchEndpoint : MatchBaseEndpoint(Endpoints.Game.MATCH_ACCEPT, HttpMethod.Post) {
    override suspend fun EndpointContext.matchBody(userId: String, match: Match) {
        match.accept(userId)
        respondSuccess()
    }
}

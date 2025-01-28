package com.arnyminerz.upv.endpoint.game

import Endpoints
import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.response.SerializableMatch
import io.ktor.http.HttpMethod

/**
 * Returns a list of all the matches being currently played by the logged-in user.
 */
object MatchEndpoint : MatchBaseEndpoint(Endpoints.Game.MATCH, HttpMethod.Get) {
    override suspend fun EndpointContext.matchBody(userId: String, match: Match) {
        respondSuccess(
            match.serializable(),
            SerializableMatch.serializer(),
        )
    }
}

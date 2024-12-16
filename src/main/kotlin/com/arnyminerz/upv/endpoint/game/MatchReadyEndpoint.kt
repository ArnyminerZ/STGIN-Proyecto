package com.arnyminerz.upv.endpoint.game

import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.database.entity.User
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.error.Errors
import io.ktor.http.HttpMethod

/**
 * Checks whether a match is ready or not.
 */
object MatchReadyEndpoint: MatchBaseEndpoint("/is_ready", HttpMethod.Get) {
    override suspend fun EndpointContext.matchBody(userId: String, match: Match) {
        respondSuccess(if (match.isReady()) "YES" else "NO")
    }
}

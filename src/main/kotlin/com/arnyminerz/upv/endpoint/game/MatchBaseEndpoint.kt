package com.arnyminerz.upv.endpoint.game

import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.database.entity.User
import com.arnyminerz.upv.database.table.Matches
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.endpoint.type.SecureEndpoint
import com.arnyminerz.upv.error.Errors
import com.arnyminerz.upv.performance.measurePerformance
import com.arnyminerz.upv.response.SerializableMatch
import io.ktor.http.HttpMethod
import io.ktor.server.util.getOrFail
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import org.jetbrains.exposed.sql.or

/**
 * Serves as a template for endpoints that handle specific matches.
 * @param operation The operation being applied to the match, can be empty (for fetching a path without applying an
 * operation), or contain a sub-path.
 * The resulting route of the endpoint will be: `/api/matches/{id}{operation}`, so `operation` must start with a `/`.
 */
abstract class MatchBaseEndpoint(
    operation: String,
    httpMethod: HttpMethod = HttpMethod.Post
): SecureEndpoint("/api/matches/{id}$operation", httpMethod) {
    final override suspend fun EndpointContext.secureBody(userId: String) {
        val matchId = call.parameters["id"]?.toIntOrNull()
        if (matchId == null) {
            respondFailure(Errors.MatchNotFound)
        }
        matchId!! // will not be null

        val match = ServerDatabase { Match.findById(matchId) }
        if (match == null) {
            respondFailure(Errors.MatchNotFound)
        }
        match!! // will not be null

        matchBody(userId, match)
    }

    protected abstract suspend fun EndpointContext.matchBody(userId: String, match: Match)
}

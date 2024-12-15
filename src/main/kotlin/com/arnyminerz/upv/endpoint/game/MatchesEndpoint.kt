package com.arnyminerz.upv.endpoint.game

import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.database.entity.User
import com.arnyminerz.upv.database.table.Matches
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.endpoint.type.SecureEndpoint
import io.ktor.http.HttpMethod
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import org.jetbrains.exposed.sql.or

/**
 * Returns a list of all the matches being currently played by the logged-in user.
 */
object MatchesEndpoint: SecureEndpoint("/api/matches", HttpMethod.Get) {
    override suspend fun EndpointContext.secureBody(user: User) {
        val matches = ServerDatabase {
            Match.find { (Matches.user1 eq user.id) or (Matches.user2 eq user.id) }.toList()
        }
        respondSuccess(
            matches.map { it.id.value },
            ListSerializer(Int.serializer())
        )
    }
}
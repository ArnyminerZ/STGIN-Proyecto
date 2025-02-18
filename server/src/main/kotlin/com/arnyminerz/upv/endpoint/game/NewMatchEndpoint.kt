package com.arnyminerz.upv.endpoint.game

import Endpoints
import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.database.entity.User
import com.arnyminerz.upv.database.table.Matches
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.endpoint.type.SecureEndpoint
import com.arnyminerz.upv.error.Errors
import com.arnyminerz.upv.game.Game
import com.arnyminerz.upv.game.Setup
import com.arnyminerz.upv.request.NewMatchRequest
import game.Board
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.server.request.contentType
import io.ktor.server.request.receiveText
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.and

/**
 * Returns a list of all the matches being currently played by the logged-in user.
 */
object NewMatchEndpoint : SecureEndpoint(Endpoints.Game.MATCHES, HttpMethod.Post) {
    override suspend fun EndpointContext.secureBody(userId: String) {
        val seed: Int
        val user2Id: String?
        if (call.request.contentType() == ContentType.Application.Json) {
            val body = call.receiveText()
            val request = Json.decodeFromString(NewMatchRequest.serializer(), body)
            seed = request.seed ?: 0
            user2Id = request.otherPlayerId
        } else {
            seed = formParameters["seed"]?.toIntOrNull() ?: 0
            user2Id = formParameters["againstUserId"].takeUnless { it == "null" }
        }

        // Make sure the user is not challenging itself
        if (userId == user2Id) {
            respondFailure(Errors.CannotMatchAgainstYourself)
        }

        // Make sure the other user exists
        var user2: User? = null
        if (user2Id != null) {
            user2 = ServerDatabase { User.findById(user2Id) }
            if (user2 == null) {
                respondFailure(Errors.UserNotFound)
            }
        }

        // Check if already has an unfinished match against the requested player.
        val anyUnfinishedMatch = ServerDatabase {
            Match.find {
                (Matches.user1 eq userId) and (Matches.user2 eq user2Id) and
                        // The game has not been finished
                        (Matches.finishedAt.isNull())
            }.limit(1).count() > 0
        }
        if (anyUnfinishedMatch) {
            respondFailure(Errors.MatchPending)
        }

        // Generate the game
        val game = Game(
            board = Board(seed),
            setupPlayer1 = Setup.empty(userId),
            setupPlayer2 = Setup.empty(user2Id),
        )

        // Create the match
        val user1 = ServerDatabase { User.findById(userId)!! }
        Match.create(game, user1, user2)

        respondSuccess()
    }
}

package com.arnyminerz.upv.endpoint.game

import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.database.entity.User
import com.arnyminerz.upv.database.table.Matches
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.endpoint.type.SecureEndpoint
import com.arnyminerz.upv.error.Errors
import com.arnyminerz.upv.game.Board
import com.arnyminerz.upv.game.Game
import com.arnyminerz.upv.game.Setup
import com.arnyminerz.upv.request.NewMatchRequest
import io.ktor.http.HttpMethod
import io.ktor.server.request.receive
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import org.jetbrains.exposed.sql.and

/**
 * Returns a list of all the matches being currently played by the logged-in user.
 */
object NewMatchEndpoint : SecureEndpoint("/api/matches", HttpMethod.Post) {
    override suspend fun EndpointContext.secureBody(userId: String) {
        val body = call.receive(NewMatchRequest::class)
        val (user2Id) = body

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
            board = Board(),
            setupPlayer1 = Setup.empty(userId),
            setupPlayer2 = Setup.empty(user2Id),
        )

        // Create the match
        ServerDatabase {
            Match.new {
                this.game = game
                this.user1 = User.findById(userId)!!
                this.user2 = user2
            }
        }

        respondSuccess()
    }
}

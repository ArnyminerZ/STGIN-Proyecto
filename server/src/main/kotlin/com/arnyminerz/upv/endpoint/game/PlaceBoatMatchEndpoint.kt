package com.arnyminerz.upv.endpoint.game

import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.database.entity.User
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.error.Errors
import com.arnyminerz.upv.game.Player
import com.arnyminerz.upv.plugins.json
import com.arnyminerz.upv.request.PlaceBoatRequest
import io.ktor.http.HttpMethod
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText

/**
 * Requests the server to start the match.
 */
object PlaceBoatMatchEndpoint : MatchBaseEndpoint("/place", HttpMethod.Post) {
    override suspend fun EndpointContext.matchBody(userId: String, match: Match) {
        if (match.startedAt != null) {
            respondFailure(Errors.MatchAlreadyStarted)
        }

        val player = match.player(userId)
        if (player == null) {
            respondFailure(Errors.NotYourMatch)
        }
        player!! // Not null

        var game = match.game
        val bodyJson = call.receiveText()
        val (boat) = json.decodeFromString(PlaceBoatRequest.serializer(), bodyJson)

        if (!boat.fits(game.board)) {
            respondFailure(Errors.PositionOutOfBounds)
        }

        val setup = game
            // Fetch the current setup for the given player
            .setup(player)
            // Place the given boat
            .placeBoat(boat)

        if (setup.anyCollision()) {
            respondFailure(Errors.ForbiddenPosition)
        }

        // Update the setup in the game
        game = if (player == Player.PLAYER1) game.copy(setupPlayer1 = setup) else game.copy(setupPlayer2 = setup)

        // Update the game in the database
        ServerDatabase { match.game = game }

        respondSuccess()
    }
}

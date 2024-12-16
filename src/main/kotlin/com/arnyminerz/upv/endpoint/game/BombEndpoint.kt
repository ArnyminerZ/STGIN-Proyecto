package com.arnyminerz.upv.endpoint.game

import com.arnyminerz.upv.ai.MachineActions
import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.error.Errors
import com.arnyminerz.upv.exception.ForbiddenPositionException
import com.arnyminerz.upv.exception.NotYourTurnException
import com.arnyminerz.upv.exception.PositionOutOfBoundsException
import com.arnyminerz.upv.game.Position
import io.ktor.http.HttpMethod

/**
 * Requests the server to start the match.
 */
object BombEndpoint : MatchBaseEndpoint("/bomb/{x}/{y}", HttpMethod.Post) {
    override suspend fun EndpointContext.matchBody(userId: String, match: Match) {
        if (match.startedAt == null) {
            respondFailure(Errors.MatchNotStarted)
        }
        if (match.finishedAt != null) {
            respondFailure(Errors.MatchAlreadyFinished)
        }

        val player = match.player(userId)
        if (player == null) {
            respondFailure(Errors.NotYourMatch)
        }
        player!! // Not null

        val parameters = call.parameters
        val x = parameters["x"]?.toIntOrNull()
        val y = parameters["y"]?.toIntOrNull()
        if (x == null || y == null) {
            respondFailure(Errors.InvalidPosition)
        }
        val position = Position(x!!, y!!)

        var game = try {
            match.game.bomb(player, position)
        } catch (_: NotYourTurnException) {
            respondFailure(Errors.NotYourTurn)
            match.game // ignored
        } catch (_: PositionOutOfBoundsException) {
            respondFailure(Errors.PositionOutOfBounds)
            match.game // ignored
        } catch (_: ForbiddenPositionException) {
            respondFailure(Errors.ForbiddenPosition)
            match.game // ignored
        }

        // If the second player is a machine, perform a bombing
        val isVsMachine = ServerDatabase { match.user2 == null }
        if (isVsMachine) {
            game = MachineActions.randomBomb(game)
        }

        val hit = game.setup(player.other()).hitsAnyBoat(position)

        // Update the game in the database
        ServerDatabase { match.game = game }

        respondSuccess(if (hit) "HIT" else "MISS")
    }
}

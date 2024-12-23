package com.arnyminerz.upv.endpoint.game

import com.arnyminerz.upv.ai.MachineActions
import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.error.Errors
import com.arnyminerz.upv.exception.ForbiddenPositionException
import com.arnyminerz.upv.exception.NotYourTurnException
import com.arnyminerz.upv.exception.PositionOutOfBoundsException
import com.arnyminerz.upv.game.Game
import com.arnyminerz.upv.game.Player
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

        val isVsMachine = ServerDatabase { match.user2 == null }

        var game = bomb(match.id.value, match.game, player, position, isVsMachine)

        // If the second player is a machine, perform a bombing
        if (isVsMachine) {
            game = MachineActions.aiBomb(match.id.value, game)
        }

        val hit = game.setup(player.other()).hitsAnyBoat(position)

        // Update the game in the database
        ServerDatabase { match.game = game }

        respondSuccess(if (hit) "HIT" else "MISS")
    }

    private suspend fun EndpointContext.bomb(
        matchId: Int,
        game: Game,
        player: Player,
        position: Position,
        isVsMachine: Boolean
    ): Game {
        return try {
            game.bomb(matchId, player, position)
        } catch (_: NotYourTurnException) {
            // If not the user's turn, but vs a machine, it means that for some reason the movement of the machine
            // was not made. Make it and call the body again
            if (isVsMachine) {
                // perform a bombing as the AI
                val newGame = MachineActions.aiBomb(matchId, game)
                // call bomb again
                bomb(matchId, newGame, player, position, true)
            } else {
                respondFailure(Errors.NotYourTurn)
                game // ignored
            }
        } catch (_: PositionOutOfBoundsException) {
            respondFailure(Errors.PositionOutOfBounds)
            game // ignored
        } catch (_: ForbiddenPositionException) {
            respondFailure(Errors.ForbiddenPosition)
            game // ignored
        }
    }
}

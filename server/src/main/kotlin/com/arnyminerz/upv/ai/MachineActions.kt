package com.arnyminerz.upv.ai

import com.arnyminerz.upv.exception.ForbiddenPositionException
import com.arnyminerz.upv.game.Game
import game.Player
import game.Position

object MachineActions {
    suspend fun aiBomb(matchId: Int, game: Game): Game = randomBomb(matchId, game)

    /**
     * Places a random bomb in an available space, and returns the updated game.
     */
    private suspend fun randomBomb(matchId: Int, game: Game): Game {
        var position = Position.random(game.board)
        var newGame: Game? = null
        while (newGame == null) {
            try {
                val updated = game.bomb(matchId, Player.PLAYER2, position)
                newGame = updated
            } catch (_: ForbiddenPositionException) {
                position = Position.random(game.board)
            }
        }
        return newGame
    }
}

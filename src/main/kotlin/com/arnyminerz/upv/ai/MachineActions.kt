package com.arnyminerz.upv.ai

import com.arnyminerz.upv.exception.ForbiddenPositionException
import com.arnyminerz.upv.game.Game
import com.arnyminerz.upv.game.Player
import com.arnyminerz.upv.game.Position

object MachineActions {
    suspend fun aiBomb(game: Game): Game = randomBomb(game)

    /**
     * Places a random bomb in an available space, and returns the updated game.
     */
    private suspend fun randomBomb(game: Game): Game {
        var position = Position.random(game.board)
        var newGame: Game? = null
        while (newGame == null) {
            try {
                val updated = game.bomb(Player.PLAYER2, position)
                newGame = updated
            } catch (_: ForbiddenPositionException) {
                position = Position.random(game.board)
            }
        }
        return newGame
    }
}

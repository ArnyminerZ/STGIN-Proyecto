package com.arnyminerz.upv.game

import com.arnyminerz.upv.exception.ForbiddenPositionException
import com.arnyminerz.upv.exception.NotYourTurnException
import com.arnyminerz.upv.exception.PositionOutOfBoundsException
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val board: Board,
    val setupPlayer1: Setup,
    val setupPlayer2: Setup,
    private var player1Bombs: Set<Position> = emptySet(),
    private var player2Bombs: Set<Position> = emptySet(),
) {
    fun isReady(vsMachine: Boolean): Boolean = setupPlayer1.isReady() && (vsMachine || setupPlayer2.isReady())

    fun turn(): Player = if (player2Bombs.size < player1Bombs.size) Player.PLAYER2 else Player.PLAYER1

    fun setup(player: Player): Setup = if (player == Player.PLAYER1) setupPlayer1 else setupPlayer2

    private fun bombs(player: Player): Set<Position> = if (player == Player.PLAYER1) player1Bombs else player2Bombs

    /**
     * Throws a bomb as the given player on the specified position.
     * Performs the following checks:
     * - A player cannot bomb its own boats.
     * - A position cannot be bombed twice.
     * @param player The player that is throwing the bomb.
     * @param position The position to throw the bomb at.
     * @return `true` if the position is a hit.
     * @throws NotYourTurnException If [player] is not the one that should make a move.
     * @throws PositionOutOfBoundsException If [position] is out of the [board]'s bounds.
     * @throws ForbiddenPositionException One of:
     * - The player is trying to hit its own boat.
     * - The player is trying to hit a position that already has a bomb.
     */
    fun bomb(player: Player, position: Position): Boolean {
        if (turn() != player) {
            throw NotYourTurnException()
        }

        if (!board.inBounds(position)) {
            throw PositionOutOfBoundsException()
        }

        val setup = setup(player)
        if (setup.hitsAnyBoat(position)) {
            throw ForbiddenPositionException("You cannot hit your own boat.")
        }

        val bombs = bombs(player)
        if (bombs.contains(position)) {
            throw ForbiddenPositionException("Already has a bomb.")
        }

        if (player == Player.PLAYER1) {
            player1Bombs += position
        } else {
            player2Bombs += position
        }
        return setup(player.other()).hitsAnyBoat(position)
    }
}

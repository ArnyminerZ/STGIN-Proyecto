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
    private val player1Bombs: Set<Position> = emptySet(),
    private val player2Bombs: Set<Position> = emptySet(),
) {
    fun isReady(vsMachine: Boolean): Boolean = setupPlayer1.isReady() && (vsMachine || setupPlayer2.isReady())

    fun turn(): Player = if (player2Bombs.size < player1Bombs.size) Player.PLAYER2 else Player.PLAYER1

    fun setup(player: Player): Setup = if (player == Player.PLAYER1) setupPlayer1 else setupPlayer2

    private fun bombs(player: Player): Set<Position> = if (player == Player.PLAYER1) player1Bombs else player2Bombs

    /**
     * Throws a bomb as the given player on the specified position.
     * Makes sure that a position is not bombed twice.
     * @param player The player that is throwing the bomb.
     * @param position The position to throw the bomb at.
     * @return The updated game instance.
     * @throws NotYourTurnException If [player] is not the one that should make a move.
     * @throws PositionOutOfBoundsException If [position] is out of the [board]'s bounds.
     * @throws ForbiddenPositionException One of:
     * - The player is trying to hit its own boat.
     * - The player is trying to hit a position that already has a bomb.
     */
    suspend fun bomb(player: Player, position: Position): Game {
        if (turn() != player) {
            throw NotYourTurnException()
        }

        if (!board.inBounds(position)) {
            throw PositionOutOfBoundsException()
        }

        val bombs = bombs(player)
        if (bombs.contains(position)) {
            throw ForbiddenPositionException("Already has a bomb.")
        }

        Orchestrator.actionsFlow.emit(
            GameAction(
                GameAction.Type.DropBomb(player, position)
            )
        )

        return if (player == Player.PLAYER1) {
            copy(player1Bombs = player1Bombs + position)
        } else {
            copy(player2Bombs = player2Bombs + position)
        }
    }
}

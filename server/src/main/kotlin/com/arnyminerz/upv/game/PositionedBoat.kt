package com.arnyminerz.upv.game

import game.Board
import game.Position
import game.PositionedBoat
import game.Rotation
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.VisibleForTesting

/**
 * Returns the positions of all the occupied cells by the boat.
 */
@VisibleForTesting
fun PositionedBoat.occupiedCells(): Set<Position> {
    return (0 until boat.length)
        .map {
            Position(
                position.x + (if (rotation == Rotation.HORIZONTAL) it else 0),
                position.y + (if (rotation == Rotation.VERTICAL) it else 0)
            )
        }
        .toSet()
}

/**
 * Determines if the current [PositionedBoat] fits within the boundaries of the provided board,
 * taking into account its position and rotation.
 *
 * @param board The board on which the boat is to be positioned.
 * @return True if the boat fits within the board boundaries, false otherwise.
 */
fun PositionedBoat.fits(board: Board): Boolean {
    val maxX = if (rotation == Rotation.HORIZONTAL) position.x + boat.length - 1 else position.x
    val maxY = if (rotation == Rotation.VERTICAL) position.y + boat.length - 1 else position.y

    return maxY < board.rows && maxX < board.columns
}

/**
 * Checks whether this positioned boat collides with another one.
 */
fun PositionedBoat.collidesWith(other: PositionedBoat): Boolean {
    val thisCells = occupiedCells()
    val otherCells = other.occupiedCells()
    return thisCells.any { it in otherCells }
}

/**
 * Checks whether a position hits the positioned boat.
 */
fun PositionedBoat.isHitBy(position: Position): Boolean {
    return position in occupiedCells()
}

package com.arnyminerz.upv.game

import kotlinx.serialization.Serializable

@Serializable
data class PositionedBoat(
    val boat: Boat,
    val position: Position,
    val rotation: Rotation
) {
    /**
     * Returns the positions of all the occupied cells by the boat.
     */
    fun occupiedCells(): Set<Position> {
        return (0U until boat.length)
            .map {
                Position(
                    position.x + (if (rotation == Rotation.HORIZONTAL) it else 0U),
                    position.y + (if (rotation == Rotation.VERTICAL) it else 0U)
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
    fun fits(board: Board): Boolean {
        val maxX = if (rotation == Rotation.HORIZONTAL) position.x + boat.length else position.x
        val maxY = if (rotation == Rotation.VERTICAL) position.y + boat.length else position.y

        return maxY < board.rows && maxX < board.columns
    }

    /**
     * Checks whether this positioned boat collides with another one.
     */
    fun collidesWith(other: PositionedBoat): Boolean {
        val thisCells = occupiedCells()
        val otherCells = other.occupiedCells()
        return thisCells.any { it in otherCells }
    }

    /**
     * Checks whether a position hits the positioned boat.
     */
    fun isHitBy(position: Position): Boolean {
        return position in occupiedCells()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PositionedBoat

        if (boat != other.boat) return false
        if (position != other.position) return false
        if (rotation != other.rotation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = boat.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + rotation.hashCode()
        return result
    }
}

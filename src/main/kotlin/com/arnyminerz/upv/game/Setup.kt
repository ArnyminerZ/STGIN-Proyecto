package com.arnyminerz.upv.game

import kotlin.random.Random
import kotlinx.serialization.Serializable

@Serializable
data class Setup(
    val positions: Set<PositionedBoat>,
    val playerId: String? = null
) {
    companion object {
        /**
         * Generates a random setup of positioned boats on the given board.
         * Ensures that all boats fit within the board boundaries and do not overlap.
         *
         * @param board The board on which the boats will be positioned.
         * @param seed The seed value used for random number generation. Defaults to 0.
         * @return A random [Setup] containing all boats positioned on the board.
         */
        fun random(board: Board, seed: Int = 0): Setup {
            val random = Random(seed)
            val allBoats = Boat.all
            var setup = Setup(emptySet())
            for (boat in allBoats) {
                val rotation = Rotation.entries.random(random)
                while (true) {
                    val x = random.nextInt(0, board.columns) - (if (rotation == Rotation.HORIZONTAL) boat.length - 1 else 0)
                    val y = random.nextInt(0, board.rows) - (if (rotation == Rotation.VERTICAL) boat.length - 1 else 0)
                    val position = Position(x, y)

                    // In theory should fit, but check just in case
                    val positionedBoat = PositionedBoat(boat, position, rotation)
                    if (!positionedBoat.fits(board)) continue

                    val newSetup = setup.copy(positions = setup.positions + positionedBoat)
                    if (newSetup.anyCollision()) continue

                    setup = newSetup
                    break
                }
            }
            return setup
        }

        /**
         * Returns a setup without positioned boats.
         */
        fun empty(playerId: String?): Setup = Setup(emptySet(), playerId)
    }

    fun isReady(): Boolean = positions.size == Boat.all.size

    fun isEmpty(): Boolean = positions.isEmpty()

    /**
     * Checks if any two boats in the setup collide with each other.
     * Iterates through all pairs of positioned boats in the setup
     * and returns true if any pair of boats occupies overlapping positions.
     *
     * @return True if a collision is detected between any two boats, false otherwise.
     */
    fun anyCollision(): Boolean {
        if (positions.size < 2) return false
        for (boat1 in positions) {
            for (boat2 in positions) {
                if (boat1 == boat2) continue
                if (boat1.collidesWith(boat2)) return true
            }
        }
        return false
    }

    /**
     * Checks whether a position hits any of the positioned boats.
     */
    fun hitsAnyBoat(position: Position): Boolean {
        return positions.any { it.isHitBy(position) }
    }

    fun placeBoat(boat: PositionedBoat): Setup {
        val positions = positions.filterNot { it.boat == boat.boat }.toMutableSet()
        positions.add(boat)
        return copy(positions = positions)
    }
}

package com.arnyminerz.upv.game

import kotlin.random.Random
import kotlinx.serialization.Serializable

@Serializable
data class Setup(
    val positions: Set<PositionedBoat>
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
                while (true) {
                    val x = random.nextInt(0, board.columns)
                    val y = random.nextInt(0, board.rows)
                    val position = Position(x, y)
                    val rotation = Rotation.entries.random(random)

                    val positionedBoat = PositionedBoat(boat, position, rotation)
                    if (!positionedBoat.fits(board)) continue

                    val newSetup = setup.copy(positions = setup.positions + positionedBoat)
                    if (!newSetup.anyCollision()) continue

                    setup = newSetup
                    break
                }
            }
            return setup
        }

        /**
         * Returns a setup without positioned boats.
         */
        fun empty(): Setup = Setup(emptySet())
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
        return copy(positions = positions + boat)
    }
}

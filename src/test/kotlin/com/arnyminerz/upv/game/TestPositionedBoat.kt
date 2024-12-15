package com.arnyminerz.upv.game

import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test

class TestPositionedBoat {
    @Test
    fun `test occupiedCells horizontal`() {
        val boat = PositionedBoat(
            boat = Boat("TestBoat", 4),
            position = Position(1, 1),
            rotation = Rotation.HORIZONTAL
        )
        assertContentEquals(
            listOf(
                Position(1, 1),
                Position(2, 1),
                Position(3, 1),
                Position(4, 1),
            ),
            boat.occupiedCells()
        )
    }

    @Test
    fun `test occupiedCells vertical`() {
        val boat = PositionedBoat(
            boat = Boat("TestBoat", 4),
            position = Position(1, 1),
            rotation = Rotation.VERTICAL
        )
        assertContentEquals(
            listOf(
                Position(1, 1),
                Position(1, 2),
                Position(1, 3),
                Position(1, 4),
            ),
            boat.occupiedCells()
        )
    }

    @Test
    fun `test fits horizontal`() {
        val boat = PositionedBoat(
            boat = Boat("TestBoat", 4),
            position = Position(0, 0),
            rotation = Rotation.HORIZONTAL
        )
        val board = Board(10, 10)

        assertTrue { boat.fits(board) }
        assertTrue { boat.copy(position = Position(6, 0)).fits(board) }
        assertFalse { boat.copy(position = Position(7, 0)).fits(board) }
        assertFalse { boat.copy(position = Position(0, 10)).fits(board) }
    }

    @Test
    fun `test fits vertical`() {
        val boat = PositionedBoat(
            boat = Boat("TestBoat", 4),
            position = Position(0, 0),
            rotation = Rotation.VERTICAL
        )
        val board = Board(10, 10)

        assertTrue { boat.fits(board) }
        assertTrue { boat.copy(position = Position(0, 6)).fits(board) }
        assertFalse { boat.copy(position = Position(0, 7)).fits(board) }
        assertFalse { boat.copy(position = Position(10, 0)).fits(board) }
    }

    @Test
    fun `test collidesWith horizontal`() {
        val boat = PositionedBoat(
            boat = Boat("TestBoat", 4),
            position = Position(0, 0),
            rotation = Rotation.HORIZONTAL
        )

        assertTrue { boat.collidesWith(boat) }
        assertTrue { boat.collidesWith(boat.copy(position = Position(1, 0))) }
        assertFalse { boat.collidesWith(boat.copy(position = Position(0, 1))) }
        assertFalse { boat.collidesWith(boat.copy(position = Position(4, 0))) }
    }

    @Test
    fun `test collidesWith vertical`() {
        val boat = PositionedBoat(
            boat = Boat("TestBoat", 4),
            position = Position(0, 0),
            rotation = Rotation.VERTICAL
        )

        assertTrue { boat.collidesWith(boat) }
        assertTrue { boat.collidesWith(boat.copy(position = Position(0, 1))) }
        assertFalse { boat.collidesWith(boat.copy(position = Position(1, 0))) }
        assertFalse { boat.collidesWith(boat.copy(position = Position(0, 4))) }
    }

    @Test
    fun `test isHitBy horizontal`() {
        val boat = PositionedBoat(
            boat = Boat("TestBoat", 4),
            position = Position(0, 0),
            rotation = Rotation.HORIZONTAL
        )

        assertTrue { boat.isHitBy(Position(0, 0)) }
        assertTrue { boat.isHitBy(Position(1, 0)) }
        assertTrue { boat.isHitBy(Position(2, 0)) }
        assertTrue { boat.isHitBy(Position(3, 0)) }
        assertFalse { boat.isHitBy(Position(4, 0)) }
        assertFalse { boat.isHitBy(Position(0, 1)) }
    }

    @Test
    fun `test isHitBy vertical`() {
        val boat = PositionedBoat(
            boat = Boat("TestBoat", 4),
            position = Position(0, 0),
            rotation = Rotation.VERTICAL
        )

        assertTrue { boat.isHitBy(Position(0, 0)) }
        assertTrue { boat.isHitBy(Position(0, 1)) }
        assertTrue { boat.isHitBy(Position(0, 2)) }
        assertTrue { boat.isHitBy(Position(0, 3)) }
        assertFalse { boat.isHitBy(Position(0, 4)) }
        assertFalse { boat.isHitBy(Position(1, 0)) }
    }
}

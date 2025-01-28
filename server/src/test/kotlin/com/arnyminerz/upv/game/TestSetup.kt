package com.arnyminerz.upv.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestSetup {
    @Test
    fun `test random`() {
        val board = Board(10, 10)
        val setup = Setup.random(board)
        assertTrue { setup.isReady() }
        assertFalse { setup.isEmpty() }
        assertFalse { setup.anyCollision() }
    }

    @Test
    fun `test empty`() {
        val setup = Setup.empty(null)
        assertFalse { setup.isReady() }
        assertTrue { setup.isEmpty() }
        assertFalse { setup.anyCollision() }
    }

    @Test
    fun `test isEmpty`() {
        assertTrue {
            Setup(emptySet()).isEmpty()
        }
        assertFalse {
            Setup(setOf(PositionedBoat(Boat("TestBoat", 2), Position(0, 0), Rotation.HORIZONTAL))).isEmpty()
        }
    }

    @Test
    fun `test anyCollision`() {
        // No collisions for empty setups
        var setup = Setup.empty(null)
        assertFalse { setup.anyCollision() }

        // No collisions when just one boat
        setup = Setup(
            setOf(
                PositionedBoat(Boat("TestBoat", 2), Position(0, 0), Rotation.HORIZONTAL)
            )
        )
        assertFalse { setup.anyCollision() }

        // No collisions in this case
        // x x q
        // o o q
        setup = Setup(
            setOf(
                PositionedBoat(Boat("TestBoat", 2), Position(0, 0), Rotation.HORIZONTAL),
                PositionedBoat(Boat("TestBoat", 2), Position(0, 1), Rotation.HORIZONTAL),
                PositionedBoat(Boat("TestBoat", 2), Position(2, 0), Rotation.VERTICAL),
            )
        )
        assertFalse { setup.anyCollision() }

        // Some collisions
        // x xq
        //   q
        setup = Setup(
            setOf(
                PositionedBoat(Boat("TestBoat", 2), Position(0, 0), Rotation.HORIZONTAL),
                PositionedBoat(Boat("TestBoat", 2), Position(1, 0), Rotation.VERTICAL)
            )
        )
        assertTrue { setup.anyCollision() }
    }

    @Test
    fun `test hitsAnyBoat`() {
        // x x q
        // o o q
        val setup = Setup(
            setOf(
                PositionedBoat(Boat("TestBoat", 2), Position(0, 0), Rotation.HORIZONTAL),
                PositionedBoat(Boat("TestBoat", 2), Position(0, 1), Rotation.HORIZONTAL),
                PositionedBoat(Boat("TestBoat", 2), Position(2, 0), Rotation.VERTICAL),
            )
        )
        assertTrue { setup.hitsAnyBoat(Position(0, 0)) }
        assertTrue { setup.hitsAnyBoat(Position(2, 1)) }
        assertFalse { setup.hitsAnyBoat(Position(3, 0)) }
        assertFalse { setup.hitsAnyBoat(Position(0, 2)) }
    }

    @Test
    fun `test placeBoat`() {
        var setup = Setup(emptySet())
        assertTrue { setup.isEmpty() }

        // Place one boat
        setup = setup.placeBoat(PositionedBoat(Boat("TestBoat1", 2), Position(0, 0), Rotation.HORIZONTAL))
        assertEquals(1, setup.positions.size)

        // Place another boat
        setup = setup.placeBoat(PositionedBoat(Boat("TestBoat2", 2), Position(0, 0), Rotation.HORIZONTAL))
        assertEquals(2, setup.positions.size)

        // Place the first boat on another position
        setup = setup.placeBoat(PositionedBoat(Boat("TestBoat1", 2), Position(2, 0), Rotation.HORIZONTAL))
        assertEquals(2, setup.positions.size)
    }
}

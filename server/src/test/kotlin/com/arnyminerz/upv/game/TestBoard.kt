package com.arnyminerz.upv.game

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestBoard {
    @Test
    fun `test inBounds`() {
        val board = Board(0, 10, 10)
        assertTrue { board.inBounds(Position(0, 0)) }
        assertTrue { board.inBounds(Position(9, 9)) }
        assertFalse { board.inBounds(Position(10, 0)) }
        assertFalse { board.inBounds(Position(0, 10)) }
    }
}

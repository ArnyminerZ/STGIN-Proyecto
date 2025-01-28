package com.arnyminerz.upv.game

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.Test

class TestPosition {
    @Test
    fun `test equals`() {
        val position1 = Position(1, 1)
        val position2 = Position(1, 1)
        val position3 = Position(0, 1)
        val position4 = Position(1, 0)

        assertEquals(position1, position2)
        assertNotEquals(position1, position3)
        assertNotEquals(position1, position4)
    }
}

package com.arnyminerz.upv.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TestBoat {
    @Test
    fun `test equals`() {
        val boat1 = Boat("SpeedBoat1", 1)
        val boat2 = Boat("SpeedBoat1", 1)
        val boat3 = Boat("SpeedBoat2", 1)
        val boat4 = Boat("SpeedBoat1", 2)

        assertEquals(
            boat1,
            boat2,
            "Boats with the same name and size should be equal"
        )
        assertNotEquals(
            boat1,
            boat3,
            "Boats with the same size but different name should not be equal"
        )
        assertEquals(
            boat1,
            boat4,
            "Boats with the same name but different size should be equal"
        )
    }
}

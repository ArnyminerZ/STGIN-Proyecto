package com.arnyminerz.upv.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TestGameAction {
    @Test
    fun `test DropBomb toString`() {
        val value = GameAction(1, GameAction.Type.DropBomb(Player.PLAYER1, Position(12, 34)), 123456789)
        assertEquals("ACTION:123456789:1:DropBomb:PLAYER1:12,34", value.toString())
    }

    @Test
    fun `test GameAction fromString DropBomb`() {
        val value = "ACTION:123456789:1:DropBomb:PLAYER1:12,34"
        val action = GameAction.fromString(value)
        assertEquals(1, action.matchId)
        assertEquals(123456789, action.timestamp)

        val type = action.type
        assertIs<GameAction.Type.DropBomb>(type)
        assertEquals(Player.PLAYER1, type.player)
        assertEquals(Position(12, 34), type.position)
    }
}

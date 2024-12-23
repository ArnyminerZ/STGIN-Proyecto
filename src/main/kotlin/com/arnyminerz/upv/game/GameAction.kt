package com.arnyminerz.upv.game

import kotlinx.serialization.Serializable

@Serializable
class GameAction(
    val matchId: Int,
    val type: Type,
    val timestamp: Long = System.currentTimeMillis()
) {
    @Serializable
    sealed interface Type {
        @Serializable
        data class DropBomb(val player: Player, val position: Position): Type {
            override fun toString(): String {
                return this::class.simpleName + ':' + player.name + ':' + position.x + ',' + position.y
            }
        }
    }

    override fun toString(): String {
        return "ACTION:$timestamp:$type"
    }
}

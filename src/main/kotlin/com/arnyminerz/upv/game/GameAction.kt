package com.arnyminerz.upv.game

import kotlinx.serialization.Serializable

@Serializable
class GameAction(
    val matchId: Int,
    val type: Type,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        /**
         * @throws IllegalArgumentException If [string] is not an action.
         * @throws IllegalStateException If the action type cannot be handled.
         */
        fun fromString(string: String): GameAction {
            require(string.startsWith("ACTION:")) { "Actions must begin with ACTION:" }
            val pieces = string.split(':')
            val (_, timestamp, matchId) = pieces
            val type = pieces.subList(3, pieces.size).joinToString(":")
            return GameAction(matchId.toInt(), Type.fromString(type), timestamp.toLong())
        }
    }

    @Serializable
    sealed interface Type {
        companion object {
            fun fromString(typeString: String): Type {
                when (val type = typeString.substringBefore(':')) {
                    DropBomb::class.simpleName -> {
                        val (_, playerName, position) = typeString.split(':')
                        val (xPos, yPos) = position.split(',')
                        val player = Player.valueOf(playerName)
                        return DropBomb(player, Position(xPos, yPos))
                    }
                    else -> error("Invalid type: $type")
                }
            }
        }

        @Serializable
        data class DropBomb(val player: Player, val position: Position): Type {
            override fun toString(): String {
                return this::class.simpleName + ':' + player.name + ':' + position.x + ',' + position.y
            }
        }
    }

    override fun toString(): String {
        return "ACTION:$timestamp:$matchId:$type"
    }
}

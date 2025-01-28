package com.arnyminerz.upv.game

import game.Player
import kotlinx.serialization.Serializable

@Serializable
class GameState(
    val matchId: Int,
    val state: State,
    val timestamp: Long = System.currentTimeMillis()
) {
    @Serializable
    sealed interface State {
        /**
         * The game is still not complete, players still need to place some boats.
         */
        data object Preparation : State {
            override fun toString(): String {
                return "PREPARATION"
            }
        }

        /**
         * The game is ready to be started, all players' boats have been positioned, the start has to be requested.
         */
        data object Ready : State {
            override fun toString(): String {
                return "READY"
            }
        }

        /**
         * The game has been started, and is being played.
         */
        data object Playing : State {
            override fun toString(): String {
                return "PLAYING"
            }
        }

        /**
         * The game has ended, [winner] is the player that won.
         */
        data class Ended(val winner: Player) : State {
            override fun toString(): String {
                return "ENDED:$winner"
            }
        }
    }

    override fun toString(): String {
        return "STATE:$timestamp:$matchId:$state"
    }
}

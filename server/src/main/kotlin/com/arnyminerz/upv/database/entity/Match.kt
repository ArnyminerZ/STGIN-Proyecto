package com.arnyminerz.upv.database.entity

import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.table.Matches
import com.arnyminerz.upv.game.GameState
import com.arnyminerz.upv.game.Orchestrator
import com.arnyminerz.upv.game.Setup
import com.arnyminerz.upv.response.SerializableMatch
import game.Player
import java.time.Instant
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Match(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Match>(Matches)

    val createdAt by Matches.createdAt

    var startedAt by Matches.startedAt
        private set
    var finishedAt by Matches.finishedAt
    var winner by Matches.winner

    var game by Matches.game

    var user1 by User referencedOn Matches.user1
    var user2 by User optionalReferencedOn Matches.user2

    var user1Ready by Matches.user1Ready
    var user2Ready by Matches.user2Ready

    suspend fun isReady(): Boolean = ServerDatabase { game.isReady(user2 == null) }

    suspend fun isReady(player: Player): Boolean = ServerDatabase { game.isReady(player) }

    suspend fun markReady(userId: String) {
        val player = player(userId) ?: error("Could not mark the machine as ready")
        check(isReady(player)) { "Player is not ready" }

        ServerDatabase {
            if (user1.id.value == userId) user1Ready = true
            else if (user2?.id?.value == userId) user2Ready = true
            else error("Invalid userId: $userId")
        }

        notifyState(GameState.State.Preparation(user1Ready, user2Ready))
    }

    /**
     * Starts the match by performing necessary setup and validations.
     * If the second user is not present, a random setup is generated for player 2.
     *
     * @param seed An optional seed value for random number generation. Defaults to 0. Used for consistent randomization
     * of the second player's setup if they are not present.
     * @throws IllegalStateException If the match is not ready to start, as determined by the `isReady` method.
     */
    suspend fun start() = ServerDatabase {
        check(isReady()) { "The game is not ready to start!" }

        if (user2 == null) {
            // randomize the game
            game = game.copy(setupPlayer2 = Setup.random(game.board))
        }

        startedAt = Instant.now()

        notifyState(GameState.State.Ready)
    }

    suspend fun serializable(): SerializableMatch = ServerDatabase {
        SerializableMatch(
            this@Match.id.value,
            createdAt.toEpochMilli(),
            isReady(),
            startedAt?.toEpochMilli(),
            finishedAt?.toEpochMilli(),
            game,
            user1.id.value,
            user2?.id?.value,
        )
    }

    /**
     * Returns the player associated with the given [userId].
     * May return null if the given [userId] is not in the match.
     */
    suspend fun player(userId: String): Player? = ServerDatabase {
        when (userId) {
            user1.id.value -> Player.PLAYER1
            user2?.id?.value -> Player.PLAYER2
            else -> null
        }
    }

    suspend fun notifyState(state: GameState.State) = Orchestrator.notifyState(matchId = this@Match.id.value, state)
}

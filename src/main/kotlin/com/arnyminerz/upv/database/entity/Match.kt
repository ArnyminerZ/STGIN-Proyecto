package com.arnyminerz.upv.database.entity

import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.table.Matches
import com.arnyminerz.upv.database.table.UserSessions
import com.arnyminerz.upv.game.Player
import com.arnyminerz.upv.game.Setup
import com.arnyminerz.upv.response.SerializableMatch
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

    var game by Matches.game

    var user1 by User referencedOn Matches.user1
    var user2 by User optionalReferencedOn Matches.user2

    fun isReady(): Boolean = game.isReady(user2 == null)

    /**
     * Starts the match by performing necessary setup and validations.
     * If the second user is not present, a random setup is generated for player 2.
     *
     * @param seed An optional seed value for random number generation. Defaults to 0. Used for consistent randomization
     * of the second player's setup if they are not present.
     * @throws IllegalStateException If the match is not ready to start, as determined by the `isReady` method.
     */
    fun start(seed: Int = 0) = ServerDatabase {
        check(isReady()) { "The game is not ready to start!" }

        if (user2 == null) {
            // randomize the game
            game = game.copy(setupPlayer2 = Setup.random(game.board, seed))
        }

        startedAt = Instant.now()
    }

    fun serializable(): SerializableMatch {
        return SerializableMatch(
            id.value,
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
    fun player(userId: String): Player? {
        return when (userId) {
            user1.id.value -> Player.PLAYER1
            user2?.id?.value -> Player.PLAYER2
            else -> null
        }
    }
}

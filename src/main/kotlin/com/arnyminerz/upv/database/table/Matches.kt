package com.arnyminerz.upv.database.table

import com.arnyminerz.upv.game.Game
import com.arnyminerz.upv.plugins.json
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.json

/**
 * Stores all the games between users or the machine.
 */
object Matches: IntIdTable("Matches") {
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    val startedAt = timestamp("started_at").nullable().default(null)
    val finishedAt = timestamp("finished_at").nullable().default(null)

    val game = json("game", json, Game.serializer())

    /**
     * Represents a reference to the first user participating in a match.
     */
    val user1 = reference("user1", Users)

    /**
     * Represents a nullable reference to a second user associated with a match in the `Matches` table.
     * This allows for the possibility that a match may not involve a second user (e.g., a game against the machine).
     */
    val user2 = reference("user2", Users).nullable()
}

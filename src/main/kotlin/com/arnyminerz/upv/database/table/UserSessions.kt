package com.arnyminerz.upv.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object UserSessions : IntIdTable() {
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    val lastSeen = timestamp("last_seen").defaultExpression(CurrentTimestamp)

    val ip = varchar("ip", 16)
    val userAgent = varchar("user_agent", 256)

    val user = reference("user", Users)
}

package com.arnyminerz.upv.database.entity

import com.arnyminerz.upv.database.table.UserSessions
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserSession(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserSession>(UserSessions)

    val createdAt by UserSessions.createdAt

    var lastSeen by UserSessions.lastSeen

    var ip by UserSessions.ip
    var userAgent by UserSessions.userAgent

    var user by User referencedOn UserSessions.user
}

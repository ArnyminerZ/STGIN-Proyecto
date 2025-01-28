package com.arnyminerz.upv.database.entity

import com.arnyminerz.upv.database.table.Users
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class User(username: EntityID<String>): Entity<String>(username) {
    companion object : EntityClass<String, User>(Users)

    var salt by Users.salt
    var hash by Users.hash
}

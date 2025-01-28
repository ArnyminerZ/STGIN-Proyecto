package com.arnyminerz.upv.database.table

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object Users: IdTable<String>("Users") {
    override val id: Column<EntityID<String>> = varchar("username", 32).entityId().uniqueIndex()

    val salt = binary("salt", 16)
    val hash = binary("hash")

    override val primaryKey: PrimaryKey = PrimaryKey(id, name = "PK_Users_ID")
}

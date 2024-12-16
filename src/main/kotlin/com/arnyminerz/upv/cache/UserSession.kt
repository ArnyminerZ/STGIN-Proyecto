package com.arnyminerz.upv.cache

import java.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
@ExperimentalUuidApi
data class UserSession(
    val uuid: Uuid = Uuid.random(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastSeen: Long = System.currentTimeMillis(),
    val expiresAt: Long,
    val ip: String,
    val userAgent: String?,
    val userId: String
) {
    companion object {
        fun new(
            expiresAt: Instant,
            ip: String,
            userAgent: String?,
            userId: String,
            uuid: Uuid = Uuid.random()
        ) = UserSession(
            uuid = uuid,
            expiresAt = expiresAt.toEpochMilli(),
            ip = ip,
            userAgent = userAgent,
            userId = userId,
        )
    }
}

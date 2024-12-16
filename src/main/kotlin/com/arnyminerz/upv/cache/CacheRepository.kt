package com.arnyminerz.upv.cache

import com.arnyminerz.upv.plugins.json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
object CacheRepository {
    suspend fun getSessionByUUID(uuid: Uuid): UserSession? {
        val rawSession = Cache.get("session:$uuid") ?: return null
        return json.decodeFromString(UserSession.serializer(), rawSession)
    }

    suspend fun setSession(session: UserSession) {
        val sessionJson = json.encodeToString(UserSession.serializer(), session)
        Cache.set("session:${session.uuid}", sessionJson)
    }

    suspend fun updateLastSeen(session: UserSession) {
        setSession(session.copy(lastSeen = System.currentTimeMillis()))
    }
}

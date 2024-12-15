package com.arnyminerz.upv.response

import com.arnyminerz.upv.game.Game
import kotlinx.serialization.Serializable

@Serializable
data class SerializableMatch(
    val id: Int,
    val createdAt: Long,
    val ready: Boolean,
    val startedAt: Long?,
    val finishedAt: Long?,
    val game: Game,
    val user1Id: String,
    val user2Id: String?
)
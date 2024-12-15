package com.arnyminerz.upv.request

import kotlinx.serialization.Serializable

/**
 * Holds the data required to play a match against another player.
 * If [otherPlayerId] is set to null, the user will play against an AI.
 */
@Serializable
data class NewMatchRequest(
    val otherPlayerId: String?
)

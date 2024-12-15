package com.arnyminerz.upv.request

import com.arnyminerz.upv.game.PositionedBoat
import kotlinx.serialization.Serializable

@Serializable
data class PlaceBoatRequest(
    val boat: PositionedBoat
)

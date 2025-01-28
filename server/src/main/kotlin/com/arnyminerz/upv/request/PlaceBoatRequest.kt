package com.arnyminerz.upv.request

import game.PositionedBoat
import kotlinx.serialization.Serializable

@Serializable
data class PlaceBoatRequest(
    val boat: PositionedBoat
)

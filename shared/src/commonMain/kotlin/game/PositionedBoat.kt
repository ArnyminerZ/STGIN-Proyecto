package game

import kotlinx.serialization.Serializable

@Serializable
data class PositionedBoat(
    val boat: Boat,
    val position: Position,
    val rotation: Rotation
)

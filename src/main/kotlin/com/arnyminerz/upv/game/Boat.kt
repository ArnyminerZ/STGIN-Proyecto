package com.arnyminerz.upv.game

import kotlinx.serialization.Serializable

@Serializable
sealed class Boat(
    val name: String,
    val length: UInt
) {
    companion object {
        val all = setOf(SpeedBoat1, SpeedBoat2, SailBoat, Yacht, Cruiser, CargoShip)

        data object SpeedBoat1: Boat("SpeedBoat1", 1u)
        data object SpeedBoat2: Boat("SpeedBoat2", 1u)
        data object SailBoat: Boat("SailBoat", 2u)
        data object Yacht: Boat("Yacht", 3u)
        data object Cruiser: Boat("Cruiser", 4u)
        data object CargoShip: Boat("CargoShip", 5u)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Boat

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

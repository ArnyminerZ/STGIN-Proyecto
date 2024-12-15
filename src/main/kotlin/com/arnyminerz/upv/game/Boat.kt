package com.arnyminerz.upv.game

import kotlinx.serialization.Serializable

@Serializable
class Boat(
    val name: String,
    val length: Int
) {
    companion object {
        private val SpeedBoat1 = Boat("SpeedBoat1", 1)
        private val SpeedBoat2 = Boat("SpeedBoat2", 1)
        private val SailBoat = Boat("SailBoat", 2)
        private val Yacht = Boat("Yacht", 3)
        private val Cruiser = Boat("Cruiser", 4)
        private val CargoShip = Boat("CargoShip", 5)

        val all = setOf(SpeedBoat1, SpeedBoat2, SailBoat, Yacht, Cruiser, CargoShip)
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

    override fun toString(): String = name
}

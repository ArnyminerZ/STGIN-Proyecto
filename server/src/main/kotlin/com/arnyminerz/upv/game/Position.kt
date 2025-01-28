package com.arnyminerz.upv.game

import kotlinx.serialization.Serializable

@Serializable
data class Position(
    val x: Int,
    val y: Int
) {
    companion object {
        fun random(board: Board): Position {
            val x = (0 until board.columns).random()
            val y = (0 until board.rows).random()
            return Position(x, y)
        }
    }

    constructor(x: String, y: String): this(x.toInt(), y.toInt())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}

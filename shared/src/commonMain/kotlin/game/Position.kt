package game

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

}

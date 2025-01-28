package game

import kotlinx.serialization.Serializable

@Serializable
class Board(
    val seed: Int = 0,
    val rows: Int = 10,
    val columns: Int = 10
)

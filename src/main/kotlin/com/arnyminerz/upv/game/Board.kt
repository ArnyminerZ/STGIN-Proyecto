package com.arnyminerz.upv.game

import kotlinx.serialization.Serializable

@Serializable
class Board(
    val rows: UInt = 10U,
    val columns: UInt = 10U,
) {
    fun inBounds(position: Position) = position.x < columns && position.y < rows
}

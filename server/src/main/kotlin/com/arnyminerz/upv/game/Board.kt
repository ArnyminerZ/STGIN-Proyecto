package com.arnyminerz.upv.game

import game.Board
import game.Position

fun Board.inBounds(position: Position) = position.x < columns && position.y < rows

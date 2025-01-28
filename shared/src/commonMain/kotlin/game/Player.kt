package game

enum class Player {
    PLAYER1,
    PLAYER2;

    fun other(): Player = if (this == PLAYER1) PLAYER2 else PLAYER1
}

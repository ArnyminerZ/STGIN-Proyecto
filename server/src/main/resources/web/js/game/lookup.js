/**¡
 * @param {Player} player
 * @returns {Player}
 */
export function opponent(player) {
    return player === 'PLAYER1' ? 'PLAYER2' : 'PLAYER1';
}

/**
 * Returns the bombs placed by the opponent of `username`.
 * May return null if `username` is not found on the game.
 * @param {Game} game
 * @param {Player} player
 * @returns {Position[]|null}
 */
export function playerBombs(game, player) {
    return player === 'PLAYER1' ? game.player1Bombs : game.player2Bombs;
}

/**
 * Returns the boats placed by the given player.
 * @param {Game} game
 * @param {Player} player
 * @returns {PositionedBoat[]|null}
 */
export function playerBoats(game, player) {
    return player === 'PLAYER1' ? game.setupPlayer1.positions : game.setupPlayer2.positions;
}

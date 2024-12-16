/**
 * Gets the opponent of the given username in the provided game.
 * May return null if the opponent is the machine.
 * @param {Game} game
 * @param {string} username
 * @returns {string|null}
 */
export function opponent(game, username) {
    return game.setupPlayer1.playerId === username ? game.setupPlayer2.playerId : game.setupPlayer1.playerId;
}

/**
 * Returns the bombs placed by the opponent of `username`.
 * May return null if `username` is not found on the game.
 * @param {Game} game
 * @param {string} username
 * @returns {Position[]|null}
 */
export function opponentBombs(game, username) {
    return game.setupPlayer1.playerId === username ? game.player2Bombs : game.setupPlayer2.playerId === username ? game.player1Bombs : null;
}

/**
 * Adds a new bomb to a user's bombs list.
 * @param {Game} game
 * @param {string} username
 * @param {Position} position
 */
export function addBomb(game, username, position) {
    if (game.setupPlayer1.playerId === username) {
        game.player1Bombs.push(position);
    } else if (game.setupPlayer2.playerId === username) {
        game.player2Bombs.push(position);
    }
}

/**
 * Returns the boats placed by the given user.
 * @param {Game} game
 * @param {string} username
 * @returns {PositionedBoat[]|null}
 */
export function userBoats(game, username) {
    return game.setupPlayer1.playerId === username ? game.setupPlayer1.positions :
        game.setupPlayer2.playerId === username ? game.setupPlayer2.positions : null;
}

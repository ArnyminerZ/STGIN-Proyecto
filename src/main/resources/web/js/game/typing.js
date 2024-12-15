/**
 * @typedef {Object} Board
 * @property {number} columns The width in columns of the board.
 * @property {number} rows The height in rows of the board.
 */

/**
 * @typedef {'HORIZONTAL'|'VERTICAL'} Rotation
 */

/**
 * @typedef {Object} Boat
 * @property {string} name
 * @property {number} length
 */

/**
 * @typedef {Object} Position
 * @property {number} x
 * @property {number} y
 */

/**
 * @typedef {Object} PositionedBoat
 * @property {Boat} boat
 * @property {Position} position
 * @property {Rotation} rotation
 */

/**
 * @typedef {Object} Setup
 * @property {PositionedBoat[]} positions
 * @property {string|null} playerId
 */

/**
 * @typedef {Object} Game
 * @property {Board} board
 * @property {Setup} setupPlayer1
 * @property {Setup} setupPlayer2
 * @property {Position[]} player1Bombs
 * @property {Position[]} player2Bombs
 */

/**
 * @typedef {Object} Match
 * @property {number} id
 * @property {number} createdAt
 * @property {number|null} startedAt
 * @property {number|null} finishedAt
 * @property {boolean} ready
 * @property {string} user1Id
 * @property {string|null} user2Id
 * @property {Game} game
 */

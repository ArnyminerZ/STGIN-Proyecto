import {getMatch} from "./storage.js";

/**
 * Drops a bomb in the given position.
 * @param {WebSocket} socket
 * @param {number} x
 * @param {number} y
 */
export async function bomb(socket, x, y) {
    const match = getMatch();
    const now = Date.now();

    // The player number will be overridden by the server
    socket.send(`ACTION:${now}:${match.id}:DropBomb:PLAYER1:${x},${y}`)
}

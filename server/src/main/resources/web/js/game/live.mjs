import {getMatch, getUsername, setMatch} from "./storage.js";
import {renderGame} from "./render.mjs";
import {showSnackbar} from "../ui.mjs";
import {refreshMatch} from "../main.mjs";

/**
 * Handles a received action message.
 * @param {string} cmd The full message received.
 * @param {WebSocket} socket The socket that received the command.
 * @returns {Promise<void>}
 */
export async function handleActionMessage(cmd, socket) {
    const split = cmd.split(':');
    // Make sure the message received is an action
    if (split[0] !== 'ACTION') return
    // Extract the timestamp
    const timestamp = parseInt(split[1]);
    // Extract the match id
    const matchId = split[2];
    // Extract the type
    const actionType = split[3];
    switch (actionType) {
        case 'DropBomb':
            const player = split[4];
            const position = split[5].split(',');
            const x = parseInt(position[0]);
            const y = parseInt(position[1]);
            const match = getMatch();
            let username;
            if (player === 'PLAYER1') {
                match.game.player1Bombs.push({x, y});
                username = match.game.setupPlayer1.playerId;
            } else {
                match.game.player2Bombs.push({x, y});
                username = match.game.setupPlayer2.playerId;
            }
            setMatch(match);
            console.debug('User:', username, 'dropped a bomb on', x, ',', y);
            await renderGame(match, socket);
            break;
        default:
            console.info('Got an unknown action type:', actionType);
            break;
    }
}

/**
 * Handles a received state message.
 * @param {string} cmd The full message received.
 * @param {WebSocket} socket The socket that received the command.
 * @returns {Promise<void>}
 */
export async function handleStateMessage(cmd, socket) {
    const split = cmd.split(':');
    // Make sure the message received is a state
    if (split[0] !== 'STATE') return
    // Extract the timestamp
    // const timestamp = parseInt(split[1]);
    // Extract the match id
    // const matchId = split[2];
    // Extract the type
    const stateName = split[3];
    switch (stateName) {
        case 'PREPARATION': {
            const player1Ready = split[4] === 'true';
            const player2Ready = split[5] === 'true';
            if (player1Ready && player2Ready) {
                showSnackbar('Los dos jugadores están listos')
            } else if (player1Ready) {
                showSnackbar('El jugador 1 está listo')
            } else if (player2Ready) {
                showSnackbar('El jugador 2 está listo')
            } else {
                // This is the first preparation message, it means that both users have now accepted the match.
                // Render the game
                showSnackbar('Los dos jugadores han aceptado el reto');
                const match = await refreshMatch();

                await renderGame(match, socket);
            }
            break;
        }
        case 'READY': {
            showSnackbar('Cargando partida...')

            const match = await refreshMatch();
            await renderGame(match, socket);

            break;
        }
        case 'ENDED': {
            const match = await getMatch();
            /** @type {Player} */
            const winnerPlayer = split[4];
            const winner = winnerPlayer === 'PLAYER1' ? match.user1Id : match?.user2Id;
            showSnackbar(`${winner ?? 'La Máquina'} ha ganado la partida`)

            setTimeout(() => window.location.reload(), 3000);
            break;
        }
        default:
            console.info('Got an unknown state type:', stateName);
            break;
    }
}

import {checkSession} from "./session.mjs";
import {get, post} from "./requests.js";
import {renderGame} from "./game/render.mjs";
import {getMatch, setMatch, setUsername} from "./game/storage.js";
import {showSnackbar} from "./ui.mjs";

/**
 * Creates a new match, optionally against a specific user.
 *
 * @param {string|null} [againstUserId=null] - The ID of the user to create a match against. If null, a match will be
 * created against the machine.
 * @return {Promise<Object>} A promise that resolves to an object containing the status code of the response and the
 * response text.
 */
async function newMatch(againstUserId = null) {
    const response = await post('/api/matches', {againstUserId: againstUserId});
    return {status: response.status, response: await response.text()};
}

/**
 * Requests a match to the server.
 * @param {number} matchId
 * @returns {Promise<null|Match>} A promise to the requested match, or null if not found.
 */
async function fetchMatch(matchId) {
    const response = await get(`/api/matches/${matchId}`);
    return response.ok ? await response.json() : null;
}

async function fetchMatches() {
    const response = await get('/api/matches');
    /** @type {int[]} */
    const matchesIds = await response.json();
    /** @type {Match[]} */
    const matches = [];

    for (const id of matchesIds) {
        const match = await fetchMatch(id);
        matches.push(match);
    }

    return matches
}

/**
 * Requests the server whether the match with the given id is ready or not.
 * @param {number} matchId The id of the match to check.
 * @returns {Promise<boolean>}
 */
async function isMatchReady(matchId) {
    const response = await get(`/api/matches/${matchId}/is_ready`);
    return await response.text() === 'YES';
}

/** @type {string|null} */
let username = null;

window.addEventListener('load', async () => {
    username = await checkSession('/login', null);
    if (username == null) return
    setUsername(username);

    const usernameElement = document.getElementById('username');
    usernameElement.innerText = username;
    usernameElement.classList.remove('shimmer');

    const matches = await fetchMatches();
    const pendingMatches = matches.filter(match => match.finishedAt == null);
    const startedMatch = matches.find(match => match.startedAt != null && match.finishedAt == null);

    console.info(matches);

    /** @type {HTMLButtonElement} */
    const newMatchButton = document.getElementById('newMatchButton');
    /** @type {HTMLButtonElement} */
    const startMatchButton = document.getElementById('startMatchButton');
    /** @type {HTMLHeadElement} */
    const pendingMatchMessage = document.getElementById('pendingMatch');
    /** @type {HTMLHeadElement} */
    const pendingMatchAgainstMessage = document.getElementById('pendingMatchAgainst');
    /** @type {HTMLHeadElement} */
    const startedMatchMessage = document.getElementById('startedMatch');
    /** @type {HTMLHeadElement} */
    const startedMatchAgainstMessage = document.getElementById('startedMatchAgainst');
    /** @type {HTMLDivElement} */
    const boardElement = document.getElementById('board');
    /** @type {HTMLDivElement} */
    const boatsElement = document.getElementById('boatsBox');

    /**
     * Joins the requested match by storing it into the session, and rendering the game.
     * @param {Match} match
     * @returns {Promise<void>}
     */
    async function joinMatch(match) {
        setMatch(match);

        // Establish a connection to the socket
        const socket = new WebSocket(`ws://${window.location.host}/api/matches/${match.id}/socket`);

        socket.onopen = function(e) {
            console.info('Connected to the socket!');
        };

        socket.onmessage = async function(event) {
            /** @type {string} */
            const cmd = event.data;
            console.log('WS', 'Received message:', cmd);
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
                    await renderGame(username, match, socket);
                    break;
                default:
                    console.info('Got an unknown action type:', actionType);
                    break;
            }
        };

        socket.onclose = function(event) {
            if (event.wasClean) {
                alert(`[close] Conexión cerrada limpiamente, código=${event.code} motivo=${event.reason}`);
            } else {
                // ej. El proceso del servidor se detuvo o la red está caída
                // event.code es usualmente 1006 en este caso
                alert('[close] La conexión se cayó');
                console.warn('Lost connection with socket. Code:', event.code, 'Message:', event.data)
            }
        };

        socket.onerror = function(error) {
            alert(`[error]`);
            console.error('Websocket error:', error);
        };

        await renderGame(username, match, socket);
    }

    newMatchButton.addEventListener('click', async () => {
        await newMatch();
        window.location.reload();
    });
    startMatchButton.addEventListener('click', async () => {
        showSnackbar('Iniciando partida...');
        const response = await post(`/api/matches/${getMatch().id}/start`, {});
        if (response.ok) {
            // TODO: Handle game start correctly
            window.location.reload();
        } else {
            response.json().then(text => showSnackbar(JSON.stringify(text)));
        }
    });

    console.log('Started match: ', startedMatch)

    if (startedMatch != null) {
        // There is a started match
        newMatchButton.setAttribute('disabled', 'true');
        startMatchButton.setAttribute('disabled', 'true');

        pendingMatchMessage.style.display = 'none';
        pendingMatchAgainstMessage.innerText = '';

        startedMatchMessage.style.display = 'block';
        startedMatchAgainstMessage.innerText = startedMatch.user2Id ?? 'La Máquina';

        boardElement.style.display = 'block';
        boatsElement.style.display = 'block';

        // Join it automatically
        await joinMatch(startedMatch);

        document.getElementById('matchLoadingIndicator').style.display = 'none';
    } else if (pendingMatches.length > 0) {
        // There's at least a pending match
        const pendingMatch = pendingMatches[0];

        newMatchButton.setAttribute('disabled', 'true');
        startMatchButton.removeAttribute('disabled');

        pendingMatchMessage.style.display = 'block';
        pendingMatchAgainstMessage.innerText = pendingMatch.user2Id ?? 'La Máquina';

        startedMatchMessage.style.display = 'none';
        startedMatchAgainstMessage.innerText = '';

        boardElement.style.display = 'block';
        boatsElement.style.display = 'block';

        // Join it automatically
        await joinMatch(pendingMatch);

        document.getElementById('matchLoadingIndicator').style.display = 'none';
    } else {
        // No games started or pending
        newMatchButton.removeAttribute('disabled');
        startMatchButton.setAttribute('disabled', 'true');

        pendingMatchMessage.style.display = 'none';
        startedMatchMessage.style.display = 'none';

        boardElement.style.display = 'none';
        boatsElement.style.display = 'none';

        document.getElementById('matchLoadingIndicator').style.display = 'none';
    }
});

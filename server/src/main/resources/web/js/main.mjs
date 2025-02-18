import {checkSession} from "./session.mjs";
import {get, post} from "./requests.js";
import {renderGame} from "./game/render.mjs";
import {getMatch, setMatch, setUsername} from "./game/storage.js";
import {showSnackbar} from "./ui.mjs";
import {ServerResponseException} from "./exceptions.js";
import {listAvailableOpponents} from "./game/chooser.mjs";
import {handleActionMessage, handleStateMessage} from "./game/live.mjs";

/**
 * Creates a new match, optionally against a specific user.
 *
 * @deprecated Start match using the form.
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

function loadAvailableOpponents() {
    /** @type {HTMLSelectElement} */
    const againstUserIdSelect = document.getElementById('againstUserId');
    /** @type {HTMLInputElement} */
    const seedInput = document.getElementById('seed');
    /** @type {HTMLLabelElement} */
    const seedLabel = document.querySelector('label[for="seed"]');

    againstUserIdSelect.addEventListener('input', function () {
        seedInput.style.display = againstUserIdSelect.value === 'null' ? '' : 'none';
        seedLabel.style.display = seedInput.style.display;
    });

    listAvailableOpponents().then((opponents) => {
        document.querySelector('input[name="redirectTo"]').value = `${window.location.origin}/`;

        // Remove all children from select
        for (let child of againstUserIdSelect.children) child.remove()

        /**
         * @param {string} value
         * @param {string} text
         */
        function newElement(value, text = value) {
            const element = document.createElement('option');
            element.value = value;
            element.innerText = text;
            againstUserIdSelect.append(element)
        }

        // Element for matching against the AI
        newElement('null', 'Máquina');

        // Load all the opponents
        for (const opponent of opponents) {
            newElement(opponent);
        }
    });
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
    /** @type {HTMLButtonElement} */
    const stopMatchButton = document.getElementById('stopMatchButton');
    /** @type {HTMLDialogElement} */
    const chooseOpponentDialog = document.getElementById('chooseOpponentDialog');
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

    /** @type {WebSocket} */
    let socket;

    /**
     * Joins the requested match by storing it into the session, and rendering the game.
     * @param {Match} match
     * @returns {Promise<void>}
     */
    async function joinMatch(match) {
        setMatch(match);

        // Establish a connection to the socket
        socket = new WebSocket(`ws://${window.location.host}/api/matches/${match.id}/socket`);

        socket.onopen = function (e) {
            console.info('Connected to the socket!');
        };

        socket.onmessage = async function (event) {
            /** @type {string} */
            const cmd = event.data;
            console.log('WS', 'Received message:', cmd);
            const split = cmd.split(':');
            // Extract the message type
            const messageType = split[0];
            // Extract the match id
            const matchId = split[2];

            // Ignore messages for matches that are not this one
            if (getMatch().id !== parseInt(matchId)) return

            switch (messageType) {
                case 'ACTION': {
                    await handleActionMessage(cmd, socket);
                    break;
                }
                case 'STATE': {
                    await handleStateMessage(cmd, socket);
                    break;
                }
                default: {
                    console.warn('Received an unknown message type:', messageType)
                    break;
                }
            }
        };

        socket.onclose = function (event) {
            if (event.wasClean) {
                alert(`[close] Conexión cerrada limpiamente, código=${event.code} motivo=${event.reason}`);
            } else {
                // ej. El proceso del servidor se detuvo o la red está caída
                // event.code es usualmente 1006 en este caso
                alert('[close] La conexión se cayó');
                console.warn('Lost connection with socket. Code:', event.code, 'Message:', event.data)
            }
            socket = null;
        };

        socket.onerror = function (error) {
            alert(`[error]`);
            console.error('Websocket error:', error);
        };

        await renderGame(match, socket);
    }

    newMatchButton.addEventListener('click', async () => {
        chooseOpponentDialog.showModal();
    });
    startMatchButton.addEventListener('click', async () => {
        showSnackbar('Iniciando partida...');
        try {
            await getMatch().start();
        } catch (error) {
            if (error instanceof ServerResponseException) {
                showSnackbar(error.message)
            }
        }
    });
    stopMatchButton.addEventListener('click', async () => {
        await getMatch().giveUp(socket);
    });

    loadAvailableOpponents();

    if (startedMatch != null) {
        // There is a started match
        console.log('Started match:', startedMatch);

        newMatchButton.setAttribute('disabled', 'true');
        startMatchButton.setAttribute('disabled', 'true');
        stopMatchButton.removeAttribute('disabled');

        pendingMatchMessage.style.display = 'none';
        pendingMatchAgainstMessage.innerText = '';

        startedMatchMessage.style.display = 'block';
        pendingMatchAgainstMessage.innerText = (startedMatch.user1Id === username ? startedMatch.user2Id : startedMatch.user1Id) ?? 'La Máquina';

        boardElement.style.display = 'block';
        boatsElement.style.display = 'block';

        // Join it automatically
        await joinMatch(startedMatch);

        document.getElementById('matchLoadingIndicator').style.display = 'none';
    } else if (pendingMatches.length > 0) {
        // There's at least a pending match
        const pendingMatch = pendingMatches[0];

        console.log('Pending match:', pendingMatch);

        newMatchButton.setAttribute('disabled', 'true');
        startMatchButton.removeAttribute('disabled');
        stopMatchButton.setAttribute('disabled', 'true');

        pendingMatchMessage.style.display = 'block';
        pendingMatchAgainstMessage.innerText = (pendingMatch.user1Id === username ? pendingMatch.user2Id : pendingMatch.user1Id) ?? 'La Máquina';

        startedMatchMessage.style.display = 'none';
        startedMatchAgainstMessage.innerText = '';

        boardElement.style.display = 'block';
        boatsElement.style.display = 'block';

        // Join it automatically
        await joinMatch(pendingMatch);

        document.getElementById('matchLoadingIndicator').style.display = 'none';
    } else {
        // No games started or pending
        console.log('No pending matches');

        newMatchButton.removeAttribute('disabled');
        startMatchButton.setAttribute('disabled', 'true');
        stopMatchButton.setAttribute('disabled', 'true');

        pendingMatchMessage.style.display = 'none';
        startedMatchMessage.style.display = 'none';

        boardElement.style.display = 'none';
        boatsElement.style.display = 'none';

        document.getElementById('matchLoadingIndicator').style.display = 'none';
    }
});

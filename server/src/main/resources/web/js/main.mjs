import {checkSession} from "./session.mjs";
import {get, post} from "./requests.js";
import {renderGame} from "./game/render.mjs";
import {getMatch, getPlayer, setMatch, setUsername} from "./game/storage.js";
import {setElementEnabled, showSnackbar} from "./ui.mjs";
import {ServerResponseException} from "./exceptions.js";
import {listAvailableOpponents} from "./game/chooser.mjs";
import {handleActionMessage, handleStateMessage} from "./game/live.mjs";
import {opponent} from "./game/lookup.js";
import {Match} from "./data/match.mjs";

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
    return response.ok ? Match.fromJSON(await response.json()) : null;
}

/**
 * Refreshes the current value of `getMatch` by calling the server again.
 * If there's no match stored, this function does nothing and returns `null`.
 * @returns {Promise<Match|null>}
 */
export async function refreshMatch() {
    let match = getMatch();
    if (match == null) return null;
    match = await fetchMatch(match.id);
    setMatch(match);
    return match;
}

/**
 * @returns {Promise<Match[]>}
 */
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
    const acceptedMatches = pendingMatches.filter(match => match.isAccepted());
    const startedMatch = matches.find(match => match.startedAt != null && match.finishedAt == null);

    /** @type {HTMLButtonElement} */
    const newMatchButton = document.getElementById('newMatchButton');
    /** @type {HTMLButtonElement} */
    const startMatchButton = document.getElementById('startMatchButton');
    /** @type {HTMLButtonElement} */
    const stopMatchButton = document.getElementById('stopMatchButton');
    /** @type {HTMLDialogElement} */
    const chooseOpponentDialog = document.getElementById('chooseOpponentDialog');

    /** @type {HTMLHeadElement} */
    const invitedMatchesMessage = document.getElementById('invitedMatches');
    /** @type {HTMLUListElement} */
    const invitedMatchesList = document.getElementById('invitedMatchesList');

    /** @type {HTMLHeadElement} */
    const playingMatchMessage = document.getElementById('playingMatch');
    /** @type {HTMLHeadElement} */
    const playingMatchAgainstMessage = document.getElementById('matchAgainst');
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
        socket = new WebSocket(`wss://${window.location.host}/api/matches/${match.id}/socket`);

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

        // Join it automatically
        await joinMatch(startedMatch);
    } else if (pendingMatches.length > 0) {
        // There's at least a pending match
        if (acceptedMatches.length > 0) {
            // The user has accepted or created a match
            console.info('The user is waiting the other part to accept');

            // Join the accepted match
            await joinMatch(acceptedMatches[0]);
        } else {
            // The user has at least one invitation but still has not accepted any
            setElementEnabled(newMatchButton, false)
            setElementEnabled(startMatchButton, false)
            setElementEnabled(stopMatchButton, false)

            invitedMatchesMessage.style.display = 'block';
            invitedMatchesList.style.display = 'block';

            // Remove laying children
            for (let child of invitedMatchesList.children) child.remove();

            console.info('The user has pending invitations');

            for (const match of pendingMatches) {
                console.log('Pending match:', match);
                const player = getPlayer(match);
                const against = opponent(player);

                const element = document.createElement('li');
                const link = document.createElement('a');
                link.href = '#';
                link.innerText = match.getUserId(against);
                link.addEventListener('click', async function (event) {
                    event.preventDefault();

                    try {
                        await match.accept();

                        // Join the accepted match
                        await joinMatch(await refreshMatch());
                    } catch (e) {
                        console.error(e);
                        showSnackbar(e)
                    }
                })
                element.append(link);
                invitedMatchesList.append(element);
            }
        }
    } else {
        // No games started or pending
        console.log('No pending matches');

        setElementEnabled(newMatchButton, true)
        setElementEnabled(startMatchButton, false)
        setElementEnabled(stopMatchButton, false)

        invitedMatchesMessage.style.display = 'none';
        invitedMatchesList.style.display = 'none';

        playingMatchMessage.style.display = 'none';

        boardElement.style.display = 'none';
        boatsElement.style.display = 'none';
    }
    document.getElementById('matchLoadingIndicator').style.display = 'none';
});

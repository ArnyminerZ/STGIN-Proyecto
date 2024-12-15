import {checkSession} from "./session.mjs";
import {get, post} from "./requests.js";
import {renderGame} from "./game/render.mjs";
import {getMatchId, setMatchId} from "./game/storage.js";

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

async function fetchMatches() {
    const response = await get('/api/matches');
    /** @type {int[]} */
    const matchesIds = await response.json();
    /** @type {Match[]} */
    const matches = [];

    for (const id of matchesIds) {
        const response = await get(`/api/matches/${id}`);
        /** @type {Match} */
        const match = await response.json();
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

    const usernameElement = document.getElementById('username');
    usernameElement.innerText = username;
    usernameElement.classList.remove('shimmer');

    const matches = await fetchMatches();
    const pendingMatches = matches.filter(match => match.finishedAt == null);

    console.info(matches);

    /** @type {HTMLButtonElement} */
    const newMatchButton = document.getElementById('newMatchButton');
    /** @type {HTMLButtonElement} */
    const joinMatchButton = document.getElementById('joinMatchButton');
    /** @type {HTMLButtonElement} */
    const startMatchButton = document.getElementById('startMatchButton');
    /** @type {HTMLHeadElement} */
    const pendingMatchMessage = document.getElementById('pendingMatch');
    /** @type {HTMLHeadElement} */
    const pendingMatchAgainstMessage = document.getElementById('pendingMatchAgainst');

    async function joinMatch(match) {
        setMatchId(match.id);

        joinMatchButton.setAttribute('disabled', 'true');
        newMatchButton.setAttribute('disabled', 'true');

        if (match.ready) {
            startMatchButton.removeAttribute('disabled');
        } else {
            startMatchButton.setAttribute('disabled', 'true');
        }

        await renderGame(username, match.game);
    }

    newMatchButton.addEventListener('click', async () => {
        await newMatch();
        window.location.reload();
    });
    startMatchButton.addEventListener('click', async () => {
        await post(`/api/matches/${getMatchId()}/start`, {});
    });

    if (pendingMatches.length <= 0) {
        newMatchButton.removeAttribute('disabled');
        joinMatchButton.setAttribute('disabled', 'true');

        pendingMatchMessage.style.display = 'none';

        pendingMatchAgainstMessage.innerText = 'Nadie';
        pendingMatchAgainstMessage.classList.remove('shimmer');
    } else {
        const match = pendingMatches[0];
        newMatchButton.setAttribute('disabled', 'true');
        joinMatchButton.removeAttribute('disabled');

        pendingMatchMessage.style.display = 'block';

        pendingMatchAgainstMessage.innerText = match.user2Id ?? 'La Máquina';
        pendingMatchAgainstMessage.classList.remove('shimmer');

        console.info(match.game);
        await joinMatch(match);
    }
});

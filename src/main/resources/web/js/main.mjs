import {checkSession} from "./session.mjs";
import {get, post} from "./requests.js";

/**
 * @typedef {Object} Match
 * @property {number} id
 * @property {number} createdAt
 * @property {number|null} startedAt
 * @property {number|null} finishedAt
 * @property {boolean} ready
 * @property {string} user1Id
 * @property {string|null} user2Id
 * @property {Object} game
 */

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

window.addEventListener('load', async () => {
    const username = await checkSession('/login', null);
    if (username == null) return

    const usernameElement = document.getElementById('username');
    usernameElement.innerText = username;
    usernameElement.classList.remove('shimmer');

    const matches = await fetchMatches();
    const pendingMatches = matches.filter(match => match.finishedAt == null);

    /** @type {HTMLButtonElement} */
    const newMatchButton = document.getElementById('newMatchButton');
    /** @type {HTMLButtonElement} */
    const joinMatchButton = document.getElementById('joinMatchButton');
    /** @type {HTMLHeadElement} */
    const pendingMatchMessage = document.getElementById('pendingMatch');
    /** @type {HTMLHeadElement} */
    const pendingMatchAgainstMessage = document.getElementById('pendingMatchAgainst');

    newMatchButton.addEventListener('click', async () => await newMatch());
    if (pendingMatches.length <= 0) {
        newMatchButton.removeAttribute('disabled');
        joinMatchButton.setAttribute('disabled', 'true');

        pendingMatchMessage.style.display = 'none';
    } else {
        newMatchButton.setAttribute('disabled', 'true');
        joinMatchButton.removeAttribute('disabled');

        pendingMatchMessage.style.display = 'block';

        pendingMatchAgainstMessage.innerText = pendingMatches[0].user2Id ?? 'La Máquina';
        pendingMatchAgainstMessage.classList.remove('shimmer');
    }
});

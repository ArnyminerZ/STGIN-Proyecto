import {checkSession} from "./session.mjs";
import {get, post} from "./requests.js";

const GRID_SIZE = 30;

/**
 * @typedef {Object} Board
 * @property {number} columns The width in columns of the board.
 * @property {number} rows The height in rows of the board.
 */

/**
 * @typedef {Object} Game
 * @property {Board} board
 * @property {Object} setupPlayer1
 * @property {Object} setupPlayer2
 * @property {[]} player1Bombs
 * @property {[]} player2Bombs
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

/** @type {number|null} */
let matchId = null;

async function placeBoat(boatId, column, row) {
    const boatElement = document.getElementById(boatId);
    const boatName = boatElement.getAttribute('data-boat');
    const boatSize = parseInt(boatElement.getAttribute('data-size'));
    const boatRotated = boatElement.getAttribute('data-rotated') === 'true';
    const body = {
        boat: {
            boat: { name: boatName, length: boatSize },
            position: { x: column, y: row },
            rotation: boatRotated ? 'VERTICAL' : 'HORIZONTAL'
        }
    };
    const response = await post(`/api/matches/${matchId}/place`, body);
    return response.ok;
}

/**
 * @param {Game} game
 */
function renderGame(game) {
    const board = game.board;
    console.log('Board size is', board.columns, 'columns x', board.rows, 'rows.')
    const boardWidthPx = board.columns * GRID_SIZE;
    const boardHeightPx = board.rows * GRID_SIZE;

    const boardElement = document.getElementById('board');
    boardElement.style.width = `${boardWidthPx}px`;
    boardElement.style.height = `${boardHeightPx}px`;

    // Remove all children
    boardElement.childNodes.forEach(child => child.remove());

    // Generate the grid
    for (let row = 0; row < board.rows; row++) {
        for (let column = 0; column < board.columns; column++) {
            const cellElement = document.createElement('div');
            cellElement.classList.add('cell');

            cellElement.style.position = 'absolute';
            cellElement.style.width = `${GRID_SIZE}px`;
            cellElement.style.height = `${GRID_SIZE}px`;
            cellElement.style.left = `${column * GRID_SIZE - 1}px`;
            cellElement.style.top = `${row * GRID_SIZE - 1}px`;
            cellElement.style.overflow = 'visible';

            cellElement.id = `cell-${row}-${column}`;
            cellElement.addEventListener('drop', drop);
            cellElement.addEventListener('dragover', allowDrop);

            boardElement.appendChild(cellElement);
        }
    }

    // Prepare boats
    /** @type {HTMLDivElement} */
    const boatsBox = document.getElementById('boatsBox');
    boatsBox.style.paddingLeft = `${boardWidthPx + 30}px`;
    const boats = document.getElementsByClassName('boat');
    for (/** @type {HTMLDivElement} */ const boat of boats) {
        boat.draggable = true;
        boat.addEventListener('dragstart', dragStart);
        boat.addEventListener('dragend', dragStop);
        boat.addEventListener('contextmenu', function (ev) {
            ev.preventDefault();
            // 1 ->  0px
            // 2 -> 15px
            // 3 -> 30px
            // 4 -> 45px
            // 5 -> 60px
            const size = parseInt(boat.getAttribute('data-size'));
            const rotated = boat.getAttribute('data-rotated') === 'true';
            boat.setAttribute('data-rotated', `${!rotated}`);

            const rotation = rotated ? 0 : 90;
            const translation = rotated ? 0 : ((size - 1) * 15);
            boat.style.transform = `rotate(${rotation}deg) translate(${translation}px, ${translation}px)`;
            console.log('Rotated', draggingId, 'by', rotated ? 0 : 90, 'degrees.')
        })
    }
}

// --- DRAG AND DROP LOGIC START ---
/** @type {string|null} */
let draggingId = null;

function allowDrop(ev) {
    ev.preventDefault();
}

function dragStart(ev) {
    draggingId = ev.target.id;
    ev.dataTransfer.setData("text", ev.target.id);
}

function dragStop(ev) {
    draggingId = null;
}

async function drop(ev) {
    ev.preventDefault();
    const draggedElementId = ev.dataTransfer.getData("text");
    const draggedElement = document.getElementById(draggedElementId);
    const cellId = ev.target.id;
    const [_, row, column] = cellId.split('-').map(Number);
    if (row == null || column == null) {
        // invalid cell id, probably dragged over a boat, ignore the drop
        return;
    }

    if (!await placeBoat(draggedElementId, column, row)) {
        console.warn('Invalid boat placement.')
        return;
    }

    console.info(`Dropped ${draggedElementId} on ${ev.target.id}`);
    ev.target.appendChild(draggedElement);
}

// --- DRAG AND DROP LOGIC END ---

window.addEventListener('load', async () => {
    const username = await checkSession('/login', null);
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
    /** @type {HTMLHeadElement} */
    const pendingMatchMessage = document.getElementById('pendingMatch');
    /** @type {HTMLHeadElement} */
    const pendingMatchAgainstMessage = document.getElementById('pendingMatchAgainst');

    newMatchButton.addEventListener('click', async () => await newMatch());
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
        matchId = match.id;
        renderGame(match.game);
    }
});

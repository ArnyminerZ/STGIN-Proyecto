import {checkSession} from "./session.mjs";
import {get, post} from "./requests.js";

const GRID_SIZE = 30;

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

function loadingIndicator(isLoading) {
    document.getElementById('loadingOverlay').style.display = isLoading ? 'block' : 'none';
}

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

/** @type {number|null} */
let matchId = null;

/** @type {string|null} */
let username = null;

async function requestBoatPlacing(boatId, column, row) {
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

async function placeBoat(boatId, cellId) {
    const boatElement = document.getElementById(boatId);
    const cellElement = document.getElementById(cellId);
    const [_, row, column] = cellId.split('-').map(Number);
    if (row == null || column == null) {
        // invalid cell id, probably dragged over a boat, ignore the drop
        return false;
    }

    if (!await requestBoatPlacing(boatId, column, row)) {
        return false;
    }

    cellElement.appendChild(boatElement);

    isMatchReady(matchId).then(ready => {
        /** @type {HTMLButtonElement} */
        const startMatchButton = document.getElementById('startMatchButton');

        if (ready) {
            startMatchButton.removeAttribute('disabled');
        } else {
            startMatchButton.setAttribute('disabled', 'true');
        }
    });

    return true;
}

/**
 * Updates the given boat element with the data provided. This is, transforming its CSS to match `rotated`.
 * @param {HTMLDivElement} boat The boat element to update.
 * @param {boolean} rotated If `true`, it means that the boat is vertical.
 */
function updateBoatElementRotation(boat, rotated) {
    const size = parseInt(boat.getAttribute('data-size'));
    const rotation = rotated ? 0 : 90;
    const translation = rotated ? 0 : ((size - 1) * 15);
    boat.setAttribute('data-rotated', `${!rotated}`);
    boat.style.transform = `rotate(${rotation}deg) translate(${translation}px, ${translation}px)`;
}

async function rotateBoat(boatId) {
    // 1 ->  0px
    // 2 -> 15px
    // 3 -> 30px
    // 4 -> 45px
    // 5 -> 60px
    const boat = document.getElementById(boatId);
    const rotated = boat.getAttribute('data-rotated') === 'true';

    updateBoatElementRotation(boat, !rotated);
    console.log('Rotated', draggingId, 'by', rotated ? 0 : 90, 'degrees.');

    if (!await requestBoatPlacing(boatId, 0, 0)) {
        updateBoatElementRotation(boat, rotated);
        return false;
    }

    return true;
}

/**
 * @param {Game} game
 */
async function renderGame(game) {
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
        boat.addEventListener('contextmenu', async function (ev) {
            ev.preventDefault();
            try {
                loadingIndicator(true);
                if (!await rotateBoat(boat.id)) {
                    console.warn('Invalid boat rotation.')
                }
            } finally {
                loadingIndicator(false);
            }
        })
    }

    // Set the positions of the boats from the game
    const setup = game.setupPlayer1.playerId === username ? game.setupPlayer1 :
        game.setupPlayer1.playerId === username ? game.setupPlayer2 : null;
    if (setup == null) {
        console.error('Invalid setup. Could not find player', username, 'in', game.setupPlayer1.playerId, 'and', game.setupPlayer2.playerId, '.');
        return
    }
    for (const positionedBoat of setup.positions) {
        console.log('Positioning', positionedBoat.boat.name, 'at', positionedBoat.position.x, positionedBoat.position.y, 'with rotation', positionedBoat.rotation);
        const boatElement = document.querySelector(`[data-boat=${positionedBoat.boat.name}]`);
        const cellElement = document.getElementById(`cell-${positionedBoat.position.y}-${positionedBoat.position.x}`);
        updateBoatElementRotation(boatElement, positionedBoat.rotation === 'VERTICAL');
        cellElement.appendChild(boatElement);
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
    try {
        loadingIndicator(true);
        ev.preventDefault();

        const draggedElementId = ev.dataTransfer.getData("text");
        const cellId = ev.target.id;

        if (!await placeBoat(draggedElementId, cellId)) {
            console.warn('Invalid boat placement.')
        }
    } finally {
        loadingIndicator(false);
    }
}

// --- DRAG AND DROP LOGIC END ---

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
        matchId = match.id;

        joinMatchButton.setAttribute('disabled', 'true');
        newMatchButton.setAttribute('disabled', 'true');

        if (match.ready) {
            startMatchButton.removeAttribute('disabled');
        } else {
            startMatchButton.setAttribute('disabled', 'true');
        }

        await renderGame(match.game);
    }

    newMatchButton.addEventListener('click', async () => {
        await newMatch();
        window.location.reload();
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

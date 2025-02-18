import {loadingIndicator, setElementEnabled, showSnackbar} from "../ui.mjs";
import {BoatsDragging} from "./initial_dragging.mjs";
import {rotateBoat, updateBoatElementRotation} from "./initial_setup.mjs";
import {equalPositions, positionHitsBoat} from "./math.mjs";
import {opponent, playerBoats, playerBombs} from "./lookup.js";
import {bomb} from "./playing.mjs";
import {getPlayer, getUsername} from "./storage.js";

/**
 * @callback ClickCellCallback
 * @async
 * @param {number} x
 * @param {number} y
 */

/** */
export const GRID_SIZE = 30;

/**
 * Calculates the size of a board in pixels based on its dimensions and a predefined grid size.
 *
 * @param {Object} board - The board object containing its dimensions.
 * @return {number[]} An array containing the width and height of the board in pixels [width, height].
 */
function calculateBoardSizePx(board) {
    const boardWidthPx = board.columns * GRID_SIZE;
    const boardHeightPx = board.rows * GRID_SIZE;
    return [boardWidthPx, boardHeightPx]
}

/**
 * Creates all the div elements that compose the main playing grid, where each boat can be placed.
 * @param {HTMLDivElement} boardElement
 * @param {Game} game
 * @param {Player} player
 * @param {?ClickCellCallback} onClickCell
 * @param {boolean} isEnabled
 * @param {boolean} applyLeftPadding
 */
function renderGrid(
    boardElement,
    game,
    player,
    onClickCell,
    isEnabled = false,
    applyLeftPadding = false
) {
    console.log('Rendering grid for', player);
    const board = game.board;
    console.log('Board size is', board.columns, 'columns x', board.rows, 'rows.')
    const [boardWidthPx, boardHeightPx] = calculateBoardSizePx(board);

    boardElement.style.width = `${boardWidthPx}px`;
    boardElement.style.height = `${boardHeightPx}px`;

    if (applyLeftPadding) {
        boardElement.style.marginLeft = `${boardWidthPx + 50}px`;
    }

    /** @type {Position[]|null} */
    const otherBombs = playerBombs(game, player);
    /** @type {PositionedBoat[]|null} */
    const boats = playerBoats(game, player);

    // Generate the grid
    for (let row = 0; row < board.rows; row++) {
        for (let column = 0; column < board.columns; column++) {
            /** @type {Position} */
            const position = {x: column, y: row};
            const cellElement = document.createElement('div');
            cellElement.classList.add('cell');

            /** @type {Position|null} */
            const bomb = otherBombs?.find((pos) => equalPositions(pos, position));
            if (bomb != null) {
                cellElement.classList.add('bomb');

                // check if the bomb hits a boat
                const hits = boats?.some((/** @type {PositionedBoat} */ boat) => positionHitsBoat(boat, position)) ?? false;
                if (hits) {
                    cellElement.classList.add('hit');
                }
            }

            cellElement.setAttribute('data-active', `${isEnabled}`);

            cellElement.style.position = 'absolute';
            cellElement.style.width = `${GRID_SIZE}px`;
            cellElement.style.height = `${GRID_SIZE}px`;
            cellElement.style.left = `${column * GRID_SIZE - 1}px`;
            cellElement.style.top = `${row * GRID_SIZE - 1}px`;
            cellElement.style.overflow = 'visible';

            cellElement.id = `cell-${row}-${column}-${player}`;
            cellElement.addEventListener('drop', BoatsDragging.drop);
            cellElement.addEventListener('dragover', BoatsDragging.allowDrop);

            if (onClickCell != null) {
                cellElement.addEventListener('click', async () => await onClickCell(column, row));
            }

            boardElement.appendChild(cellElement);
        }
    }
}

/**
 * Removes all existing cells.
 */
function resetCells() {
    const cells = document.getElementsByClassName('cell');
    // strange workaround, because for some reason for-loops do not work correctly
    while (cells[0]) {
        cells[0].parentNode.removeChild(cells[0]);
    }
}

/**
 * Adds all the listeners to the boat elements so that they can be dragged.
 * @param {Game} game
 */
function allowDraggingBoats(game) {
    const [boardWidthPx] = calculateBoardSizePx(game.board);

    /** @type {HTMLDivElement} */
    const boatsBox = document.getElementById('boatsBox');
    boatsBox.style.paddingLeft = `${boardWidthPx + 30}px`;
    const boats = document.getElementsByClassName('boat');
    for (/** @type {HTMLDivElement} */ const boat of boats) {
        boat.draggable = true;
        boat.addEventListener('dragstart', BoatsDragging.dragStart);
        boat.addEventListener('dragend', BoatsDragging.dragStop);
        boat.addEventListener('contextmenu', async function (ev) {
            ev.preventDefault();
            try {
                loadingIndicator(true);
                if (!await rotateBoat(boat.id)) {
                    console.warn('Invalid boat rotation.')
                    showSnackbar('No se puede rotar el barco a ahí.', 1000);
                }
            } finally {
                loadingIndicator(false);
            }
        })
    }
}

function forbidDraggingBoats() {
    const boats = document.getElementsByClassName('boat');
    for (/** @type {HTMLDivElement} */ const boat of boats) {
        boat.draggable = false;
    }
}

/**
 * Moves all the boats to their respective positions according to `game`.
 * @param {Game} game
 * @param {Player} player
 */
function moveBoats(game, player) {
    console.log('Placing boats of', player);
    const setup = player === 'PLAYER1' ? game.setupPlayer1 : game.setupPlayer2;
    console.log('Player has', setup.positions.length, 'positioned boats');
    for (const positionedBoat of setup.positions) {
        const boatElement = document.querySelector(`[data-boat=${positionedBoat.boat.name}]`);
        if (boatElement == null) {
            console.error('Could not find boat', positionedBoat.boat.name);
            continue;
        }
        const cellElement = document.getElementById(`cell-${positionedBoat.position.y}-${positionedBoat.position.x}-${player}`);
        updateBoatElementRotation(boatElement, positionedBoat.rotation === 'VERTICAL');
        boatElement.setAttribute('data-x', `${positionedBoat.position.x}`);
        boatElement.setAttribute('data-y', `${positionedBoat.position.y}`);
        cellElement.appendChild(boatElement);
    }
}

/**
 * Moves all boats back to the boats box.
 */
function resetBoats() {
    const boatsBox = document.getElementById('boatsBox');
    /** @type {HTMLDivElement[]} */
    const boats = [].slice.call(document.getElementsByClassName('boat'));
    for (const boat of boats) {
        boatsBox.appendChild(boat);
    }
}

/**
 * @param {Match} match
 * @param {WebSocket} socket
 */
export async function renderGame(match, socket) {
    const game = match.game;
    const hasStarted = match.startedAt != null;
    const isWaiting = !match.user1Accepted || !match.user2Accepted;

    /** @type {Player} */ const player = getPlayer(match);

    const opponentBoardElement = document.getElementById('opponentBoard');
    /** @type {HTMLDivElement} */
    const boardElement = document.getElementById('board');
    /** @type {HTMLDivElement} */
    const boatsElement = document.getElementById('boatsBox');

    const opponentPlayer = opponent(player);

    //<editor-fold desc="Update toolbar">
    /** @type {HTMLButtonElement} */ const newMatchButton = document.getElementById('newMatchButton');
    /** @type {HTMLButtonElement} */ const startMatchButton = document.getElementById('startMatchButton');
    /** @type {HTMLButtonElement} */ const stopMatchButton = document.getElementById('stopMatchButton');
    /** @type {HTMLHeadElement} */ const waitingForConfirmationMessage = document.getElementById('waitingForConfirmation');
    /** @type {HTMLHeadElement} */ const invitedMatchesMessage = document.getElementById('invitedMatches');
    /** @type {HTMLUListElement} */ const invitedMatchesList = document.getElementById('invitedMatchesList');
    /** @type {HTMLHeadElement} */ const playingMatchMessage = document.getElementById('playingMatch');
    /** @type {HTMLHeadElement} */ const playingMatchAgainstMessage = document.getElementById('matchAgainst');

    setElementEnabled(newMatchButton, false)
    setElementEnabled(startMatchButton, !hasStarted)
    setElementEnabled(stopMatchButton, true)

    waitingForConfirmationMessage.style.display = !isWaiting ? 'none' : '';
    invitedMatchesMessage.style.display = 'none';
    invitedMatchesList.style.display = 'none';

    playingMatchMessage.style.display = 'block';
    playingMatchAgainstMessage.innerText = (player === 'PLAYER1' ? match.user2Id : match.user1Id) ?? 'La Máquina';

    boardElement.style.display = isWaiting ? 'none' : 'block';
    boatsElement.style.display = isWaiting ? 'none' : 'block';
    //</editor-fold>

    resetBoats();
    resetCells();

    renderGrid(boardElement, game, player, null, !hasStarted);
    if (hasStarted) {
        renderGrid(
            opponentBoardElement,
            game,
            opponentPlayer,
            (x, y) => bomb(socket, x, y),
            true,
            true
        );
        opponentBoardElement.style.display = 'block';
    } else {
        opponentBoardElement.style.display = 'none';
    }

    if (match.startedAt == null) {
        allowDraggingBoats(game);
    } else {
        forbidDraggingBoats();
    }

    moveBoats(game, player);
}
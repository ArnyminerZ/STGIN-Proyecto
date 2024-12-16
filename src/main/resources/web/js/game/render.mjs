import {loadingIndicator, showSnackbar} from "../ui.mjs";
import {BoatsDragging} from "./initial_dragging.mjs";
import {rotateBoat, updateBoatElementRotation} from "./initial_setup.mjs";

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
 * @param {?ClickCellCallback} onClickCell
 * @param {boolean} isEnabled
 * @param {boolean} leftPadding
 */
function renderGrid(
    boardElement,
    game,
    onClickCell,
    isEnabled = false,
    leftPadding = false
) {
    const board = game.board;
    console.log('Board size is', board.columns, 'columns x', board.rows, 'rows.')
    const [boardWidthPx, boardHeightPx] = calculateBoardSizePx(board);

    boardElement.style.width = `${boardWidthPx}px`;
    boardElement.style.height = `${boardHeightPx}px`;

    if (leftPadding) {
        boardElement.style.marginLeft = `${boardWidthPx + 50}px`;
    }

    // Remove all children
    boardElement.childNodes.forEach(child => child.remove());

    // Generate the grid
    for (let row = 0; row < board.rows; row++) {
        for (let column = 0; column < board.columns; column++) {
            const cellElement = document.createElement('div');
            cellElement.classList.add('cell');

            cellElement.setAttribute('data-active', `${isEnabled}`);

            cellElement.style.position = 'absolute';
            cellElement.style.width = `${GRID_SIZE}px`;
            cellElement.style.height = `${GRID_SIZE}px`;
            cellElement.style.left = `${column * GRID_SIZE - 1}px`;
            cellElement.style.top = `${row * GRID_SIZE - 1}px`;
            cellElement.style.overflow = 'visible';

            cellElement.id = `cell-${row}-${column}`;
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

/**
 * Moves all the boats to their respective positions according to `game`.
 * @param {Game} game
 * @param {string} username
 */
function moveBoats(game, username) {
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
        boatElement.setAttribute('data-x', `${positionedBoat.position.x}`);
        boatElement.setAttribute('data-y', `${positionedBoat.position.y}`);
        cellElement.appendChild(boatElement);
    }
}

/**
 * @param {string} username
 * @param {Match} match
 * @param {ClickCellCallback} onClickCell Called when the game has started, and the user clicks on an opponent's cell.
 */
export async function renderGame(username, match, onClickCell) {
    const game = match.game;
    const hasStarted = match.startedAt != null;

    const boardElement = document.getElementById('board');
    const opponentBoardElement = document.getElementById('opponentBoard');
    renderGrid(boardElement, game, null, !hasStarted);
    if (hasStarted) {
        renderGrid(opponentBoardElement, game, onClickCell, true, true);
        opponentBoardElement.style.display = 'block';
    }

    if (match.startedAt == null) {
        allowDraggingBoats(game);
    }

    moveBoats(game, username);
}
/**
 * Obtains a list of the cells used by a boat.
 * @param {PositionedBoat} positionedBoat
 * @return {Position[]} A list of the positions occupied by the boat.
 */
function boatOccupiedCells(positionedBoat) {
    const position = positionedBoat.position;
    const rotation = positionedBoat.rotation;
    return Array.from(
        {length: positionedBoat.boat.length},
        (_, index) => {
            return {
                x: position.x + (rotation === 'HORIZONTAL' ? index : 0),
                y: position.y + (rotation === 'VERTICAL' ? index : 0)
            }
        })
}

/**
 * Checks whether a given position hits a positioned boat.
 * @param {PositionedBoat} boat
 * @param {Position} position
 * @return {boolean} If the position hits the boat.
 */
export function positionHitsBoat(boat, position) {
    const usedCells = boatOccupiedCells(boat);
    return usedCells.some(cell => equalPositions(cell, position));
}

/**
 * Compares two positions, and returns `true` if their components match.
 * @param {Position} pos1
 * @param {Position} pos2
 * @returns {boolean}
 */
export function equalPositions(pos1, pos2) {
    return pos1.x === pos2.x && pos1.y === pos2.y;
}

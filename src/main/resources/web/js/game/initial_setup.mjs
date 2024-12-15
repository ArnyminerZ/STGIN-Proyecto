import {post} from "../requests.js";
import {getMatchId} from "./storage.js";

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
    const matchId = getMatchId();
    const response = await post(`/api/matches/${matchId}/place`, body);
    return response.ok;
}

export async function placeBoat(boatId, cellId) {
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

    return true;
}

/**
 * Updates the given boat element with the data provided. This is, transforming its CSS to match `rotated`.
 * @param {HTMLDivElement} boat The boat element to update.
 * @param {boolean} rotated If `true`, it means that the boat is vertical.
 */
export function updateBoatElementRotation(boat, rotated) {
    const size = parseInt(boat.getAttribute('data-size'));
    const rotation = rotated ? 0 : 90;
    const translation = rotated ? 0 : ((size - 1) * 15);
    boat.setAttribute('data-rotated', `${!rotated}`);
    boat.style.transform = `rotate(${rotation}deg) translate(${translation}px, ${translation}px)`;
}

export async function rotateBoat(boatId) {
    // 1 ->  0px
    // 2 -> 15px
    // 3 -> 30px
    // 4 -> 45px
    // 5 -> 60px
    const boat = document.getElementById(boatId);
    const rotated = boat.getAttribute('data-rotated') === 'true';

    updateBoatElementRotation(boat, !rotated);

    if (!await requestBoatPlacing(boatId, 0, 0)) {
        updateBoatElementRotation(boat, rotated);
        return false;
    }

    return true;
}

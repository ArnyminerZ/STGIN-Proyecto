import {loadingIndicator, showSnackbar} from "../ui.mjs";
import {placeBoat} from "./initial_setup.mjs";

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
            showSnackbar('No se puede poner el barco ahí.', 1000);
        }
        /*isMatchReady(matchId).then(ready => {
            const startMatchButton = document.getElementById('startMatchButton');

            if (ready) {
                startMatchButton.removeAttribute('disabled');
            } else {
                startMatchButton.setAttribute('disabled', 'true');
            }
        });*/
    } finally {
        loadingIndicator(false);
    }
}

export const BoatsDragging = { allowDrop, dragStart, dragStop, drop }

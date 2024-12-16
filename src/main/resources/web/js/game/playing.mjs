import {post} from "../requests.js";
import {getMatch, getUsername, setMatch} from "./storage.js";
import {renderGame} from "./render.mjs";
import {addBomb} from "./lookup.js";

/**
 * Drops a bomb in the given position.
 * @param {number} x
 * @param {number} y
 */
export async function bomb(x, y) {
    const match = getMatch();
    const username = getUsername();

    const response = await post(`/api/matches/${match.id}/bomb/${x}/${y}`, {});
    if (!response.ok) {
        console.error(await response.json());
    } else {
        const hit = (await response.text()) === 'HIT';
        console.warn("💣", x, y, hit ? '🔥' : '💧');

        // Add the bomb to the match's game
        addBomb(match.game, username, {x, y});

        // Update the match with the new game
        setMatch(match);

        await renderGame(username, match, bomb);
    }
}

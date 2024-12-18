import {post} from "../requests.js";
import {getMatch} from "./storage.js";

/**
 * Drops a bomb in the given position.
 * @param {number} x
 * @param {number} y
 */
export async function bomb(x, y) {
    const match = getMatch();

    const response = await post(`/api/matches/${match.id}/bomb/${x}/${y}`, {});
    if (!response.ok) {
        console.error(await response.json());
    } else {
        const hit = (await response.text()) === 'HIT';
        console.warn("💣", x, y, hit ? '🔥' : '💧');
    }
}

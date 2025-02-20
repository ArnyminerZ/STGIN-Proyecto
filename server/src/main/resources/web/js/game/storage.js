import {Match} from '../data/match.mjs';

/**
 * Fetches the stored match.
 * @returns {Match|null}
 */
export function getMatch() {
    const json = sessionStorage.getItem("match");
    if (json != null) {
        return Match.fromJSONString(json);
    } else {
        return null;
    }
}

/**
 * Stores a match into the session.
 * @param {Match} match
 */
export function setMatch(match) {
    sessionStorage.setItem("match", JSON.stringify(match));
}

/**
 * Clears the currently selected match.
 */
export function clearMatch() {
    sessionStorage.removeItem("match");
}

/**
 * Fetches the stored username.
 * @returns {string|null}
 */
export function getUsername() {
    return sessionStorage.getItem("username");
}

/**
 * Stores the user's name.
 * @param {string} username
 */
export function setUsername(username) {
    sessionStorage.setItem("username", username);
}

/**
 * Clears the stored username.
 */
export function clearUsername() {
    sessionStorage.removeItem("username");
}

/**
 * Uses `getUsername` and `getMatch` to get the current player.
 * @param {Match|null} match If null, defaults to `getMatch`.
 * @return {Player}
 */
export function getPlayer(match = null) {
    const _match = match ?? getMatch();
    const username = getUsername();
    return match.user1Id === username ? 'PLAYER1' : 'PLAYER2';
}

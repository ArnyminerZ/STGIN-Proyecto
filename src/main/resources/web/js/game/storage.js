/**
 * Fetches the stored match.
 * @returns {Match|null}
 */
export function getMatch() {
    const json = sessionStorage.getItem("match");
    if (json != null) {
        return JSON.parse(json);
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

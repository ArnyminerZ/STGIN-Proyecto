import {get} from "../requests.js";

/**
 * Fetches a list of all the available opponents from the server.
 * @returns {Promise<string[]>}
 */
export async function listAvailableOpponents() {
    const response = await get('/api/opponents');
    return await response.json()
}

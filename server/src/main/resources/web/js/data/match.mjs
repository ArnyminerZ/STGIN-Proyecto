import {post} from "../requests.js";
import {ServerResponseException} from "../exceptions.js";

export class Match {
    /** @type {number} */ id
    /** @type {number} */ createdAt;
    /** @type {number|null} */ startedAt;
    /** @type {number|null} */ finishedAt;
    /** @type {boolean} */ ready;
    /** @type {string} */ user1Id;
    /** @type {string|null} */ user2Id;
    /** @type {Game} */ game;

    constructor(id, createdAt, startedAt, finishedAt, ready, user1Id, user2Id, game) {
        this.id = id
        this.createdAt = createdAt
        this.startedAt = startedAt
        this.finishedAt = finishedAt
        this.ready = ready
        this.user1Id = user1Id
        this.user2Id = user2Id
        this.game = game
    }

    /**
     * Builds a `Match` from its JSON definition.
     * @param {string} json The json object as string to parse.
     * @returns {Match}
     */
    static fromJSON(json) {
        const obj = JSON.parse(json);
        return new Match(obj.id, obj.createdAt, obj.startedAt, obj.finishedAt, obj.ready, obj.user1Id, obj.user2Id, obj.game)
    }

    async start() {
        const response = await post(`/api/matches/${this.id}/start`, {});
        if (response.ok) {
            // TODO: Handle game start correctly
            // window.location.reload();
        } else {
            const error = await response.json();
            throw new ServerResponseException(JSON.stringify(error))
        }
    }
}

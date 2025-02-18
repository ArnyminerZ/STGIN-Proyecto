import {post} from "../requests.js";
import {ServerResponseException} from "../exceptions.js";
import {getPlayer, getUsername} from "../game/storage.js";

export class Match {
    /** @type {number} */ id
    /** @type {number} */ createdAt;
    /** @type {number|null} */ startedAt;
    /** @type {number|null} */ finishedAt;
    /** @type {boolean} */ ready;
    /** @type {string} */ user1Id;
    /** @type {boolean} */ user1Accepted;
    /** @type {boolean} */ user1Ready;
    /** @type {string|null} */ user2Id;
    /** @type {boolean} */ user2Accepted;
    /** @type {boolean} */ user2Ready;
    /** @type {Game} */ game;

    constructor(id, createdAt, startedAt, finishedAt, ready, user1Id, user1Accepted, user1Ready, user2Id, user2Accepted, user2Ready, game) {
        this.id = id
        this.createdAt = createdAt
        this.startedAt = startedAt
        this.finishedAt = finishedAt
        this.ready = ready

        this.user1Id = user1Id
        this.user1Accepted = user1Accepted
        this.user1Ready = user1Ready

        this.user2Id = user2Id
        this.user2Accepted = user2Accepted
        this.user2Ready = user2Ready

        this.game = game
    }

    /**
     * Builds a `Match` from its JSON definition.
     * @param {Object} json The parsed json object.
     * @returns {Match}
     */
    static fromJSON(json) {
        return new Match(
            json.id,
            json.createdAt,
            json.startedAt,
            json.finishedAt,
            json.ready,
            json.user1Id,
            json.user1Accepted,
            json.user1Ready,
            json.user2Id,
            json.user2Accepted,
            json.user2Ready,
            json.game,
        )
    }

    /**
     * Builds a `Match` from its JSON definition.
     * @param {string} json The json object as string to parse.
     * @returns {Match}
     */
    static fromJSONString(json) {
        const obj = JSON.parse(json);
        return Match.fromJSON(obj);
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

    async accept() {
        const response = await post(`/api/matches/${this.id}/accept`, {});
        if (!response.ok) {
            const error = await response.json();
            throw new ServerResponseException(JSON.stringify(error))
        }
    }

    /**
     * Sends a give up command through the socket.
     * @param {WebSocket} socket
     * @returns {Promise<void>}
     */
    async giveUp(socket) {
        const now = Date.now();
        const player = getPlayer(this);
        console.info('Giving up as', player);
        socket.send(`ACTION:${now}:${this.id}:GiveUp:${player}`)
    }

    /**
     * Checks whether the current user has accepted this match.
     * @returns {boolean}
     */
    isAccepted() {
        const username = getUsername();
        return (this.user1Id === username && this.user1Accepted) || (this.user2Id === username && this.user2Accepted)
    }

    /**
     * Returns the user id of the given player.
     * @param {Player} player
     * @return {string|null} The user id matching `player`. May be null if `player` is the machine.
     */
    getUserId(player) {
        return player === 'PLAYER1' ? this.user1Id : this.user2Id;
    }
}

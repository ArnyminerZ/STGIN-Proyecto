export class ServerResponseException extends Error {
    constructor(message) {
        super(message);
        this.name = this.constructor.name;
    }
}

/**
 * Sends an HTTP POST request to the specified path with the provided body.
 *
 * @param {string} path - The URL or endpoint to which the POST request is sent.
 * @param {Object} body - The request payload to be sent as the body of the POST request.
 * @return {Promise<Response>} A promise that resolves to the response of the fetch request.
 */
export function post(path, body) {
    return fetch(
        path,
        {
            method: 'POST',
            body: JSON.stringify(body),
            headers: {
                "Content-Type": "application/json",
            },
        }
    );
}

export function get(path) {
    return fetch(path, {method: 'GET'});
}

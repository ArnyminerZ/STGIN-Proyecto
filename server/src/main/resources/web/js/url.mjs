/**
 * Parses the query string from the current window's URL and returns an object
 * where the keys are the parameter names and the values are the decoded parameter values.
 *
 * @return {Object} An object representing the query parameters from the URL.
 */
export function queryString() {
    const query = window.location.search.substring(1); // remove '?' prefix
    const params = {};

    if (query) {
        const pairs = query.split('&');
        for (const pair of pairs) {
            const [key, value] = pair.split('=');
            params[key] = decodeURIComponent(value.replace(/\+/g, ' '));
        }
    }

    return params;
}

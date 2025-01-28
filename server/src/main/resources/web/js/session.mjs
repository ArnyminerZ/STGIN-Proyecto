/**
 * Validates the current session, and redirects to `loginRedirection` if the user is not currently logged in.
 * If the user is logged in, redirects to `loggedRedirection`.
 * Set any of them to null to disable redirection.
 * @param {?string} loginRedirection The url to redirect to if the user is not logged in.
 * @param {?string} loggedRedirection The url to redirect to if the user is logged in.
 * @return {?string} The currently logged-in user's username, or null if not logged in.
 */
export async function checkSession(
    loginRedirection = '/login',
    loggedRedirection = null
) {
    const response = await fetch('/api/auth/session', {
        method: 'GET',
    });
    if (response.ok) {
        if (loggedRedirection !== null) {
            window.location.href = loggedRedirection;
        }
        return await response.text()
    } else {
        if (loginRedirection !== null) {
            window.location.href = loginRedirection;
        }
        return null;
    }
}

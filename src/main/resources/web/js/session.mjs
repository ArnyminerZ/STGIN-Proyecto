/**
 * Validates the current session, and redirects to `loginRedirection` if the user is not currently logged in.
 * If the user is logged in, redirects to `loggedRedirection`.
 * Set any of them to null to disable redirection.
 * @param {?string} loginRedirection The url to redirect to if the user is not logged in.
 * @param {?string} loggedRedirection The url to redirect to if the user is logged in.
 */
export async function checkSession(
    loginRedirection = '/login',
    loggedRedirection = null
) {
    const response = await fetch('/api/auth/session', {
        method: 'GET',
    });
    if (response.status === 200) {
        if (loggedRedirection !== null) {
            window.location.href = loggedRedirection;
        }
    } else {
        if (loginRedirection !== null) {
            window.location.href = loginRedirection;
        }
    }
}

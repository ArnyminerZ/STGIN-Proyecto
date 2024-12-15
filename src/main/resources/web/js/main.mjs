import {checkSession} from "./session.mjs";

async function newMatch(againstUserId = null) {
    const response = await fetch(
        '/api/matches',
        {
            method: 'POST',
            body: JSON.stringify({otherPlayerId: againstUserId}),
            headers: {
                "Content-Type": "application/json",
            },
        }
    );
    return {status: response.status, response: await response.text()};
}

window.addEventListener('load', async () => {
    const username = await checkSession('/login', null);
    if (username == null) return

    const usernameElement = document.getElementById('username');
    usernameElement.innerText = username;
    usernameElement.classList.remove('shimmer');

    document.getElementById('newMatchButton')
        .addEventListener('click', async () => {
            await newMatch()
        })
});

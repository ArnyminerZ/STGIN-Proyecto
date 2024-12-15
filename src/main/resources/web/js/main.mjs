import {checkSession} from "./session.mjs";

window.addEventListener('load', async () => {
    const username = await checkSession('/login', null);
    if (username == null) return

    const usernameElement = document.getElementById('username');
    usernameElement.innerText = username;
    usernameElement.classList.remove('shimmer');
});

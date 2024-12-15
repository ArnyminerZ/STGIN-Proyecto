import {checkSession} from "./session.mjs";

window.addEventListener('load', async () => {
    await checkSession('/login', null);
});

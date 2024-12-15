import {checkSession} from "./session.mjs";
import {showErrorInField} from './errors.mjs';

window.addEventListener('load', async () => {
    await checkSession(null, '/');

    // Update the values in the form
    document.querySelector('input[name="redirectTo"]').value = `${window.location.origin}/login`;
    document.getElementById('username').value = sessionStorage.getItem('username');
    document.getElementById('password').value = sessionStorage.getItem('password');

    // Store the username and password in session when submitting.
    // This allows setting the original values back after redirecting to the api.
    document.querySelector('form').addEventListener('submit', () => {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        sessionStorage.setItem('username', username);
        sessionStorage.setItem('password', password);
    });

    // Show errors in form
    showErrorInField('registerError');
});

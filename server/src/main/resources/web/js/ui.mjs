/**
 * Shows or hides the loading overlay.
 * @param {boolean} isLoading If `true`, the overlay will be shown, if `false`, it will be hidden.
 */
export function loadingIndicator(isLoading) {
    document.getElementById('loadingOverlay').style.display = isLoading ? 'block' : 'none';
}

/**
 * Shows the snackbar with the given text for the desired amount of milliseconds.
 * @param {string} text The text to show on the snackbar.
 * @param {number} timeout The amount of time in milliseconds to display the snackbar for.
 */
export function showSnackbar(text, timeout = 3000) {
    // Get the snackbar DIV
    const x = document.getElementById("snackbar");

    // Add the "show" class to DIV
    x.className = "show";
    x.innerText = text;

    // After 3 seconds, remove the show class from DIV
    setTimeout(() =>{ x.className = x.className.replace("show", ""); }, timeout);
}

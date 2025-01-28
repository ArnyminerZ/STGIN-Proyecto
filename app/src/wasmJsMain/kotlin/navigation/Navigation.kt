package navigation

import androidx.annotation.MainThread
import kotlinx.browser.window

@MainThread
fun <T : Destination> navigateToDestination(
    route: T,
    isSingleTop: Boolean = false
) {
    if (isSingleTop) {
        // This clears history
        window.history.back()
    }
    window.location.assign(route.path)
}

@MainThread
fun navigateUp() {
    window.history.back()
}

object Endpoints {
    const val ROOT = "/"

    object Auth {
        const val LOGIN = "/auth/login"
        const val REGISTER = "/auth/register"
        const val LOGOUT = "/auth/logout"
        const val SESSION = "/auth/session"
    }

    object Game {
        const val OPPONENTS = "/opponents"

        const val GAME = "/matches/{id}/socket"
        const val MATCHES = "/matches"
        const val MATCH = "/matches/{id}"
        const val MATCH_READY = "/matches/{id}/is_ready"
        const val MATCH_PLACE = "/matches/{id}/place"
        const val MATCH_START = "/matches/{id}/start"
    }
}

object Endpoints {
    const val ROOT = "/"

    object Auth {
        const val LOGIN = "/api/auth/login"
        const val REGISTER = "/api/auth/register"
        const val LOGOUT = "/api/auth/logout"
    }

    object Game {
        const val GAME = "/api/matches/{id}/socket"
        const val MATCHES = "/api/matches"
        const val MATCH = "/api/matches/{id}"
        const val MATCH_READY = "/api/matches/{id}/is_ready"
        const val MATCH_PLACE = "/api/matches/{id}/place"
        const val MATCH_START = "/api/matches/{id}/start"
    }
}

package error

object ErrorCodes {
    const val MISSING_CREDENTIALS = 1
    const val INVALID_CREDENTIALS = 2
    const val USER_ALREADY_EXISTS = 3
    const val NOT_LOGGED_IN = 4
    const val MATCH_NOT_FOUND = 5
    const val CANNOT_MATCH_AGAINST_YOURSELF = 6
    const val USER_NOT_FOUND = 7
    const val MATCH_PENDING = 8
    const val MATCH_NOT_READY = 9
    const val MATCH_ALREADY_STARTED = 10
    const val POSITION_OUT_OF_BOUNDS = 11
    const val FORBIDDEN_POSITION = 12
    const val NOT_YOUR_MATCH = 13
    const val MATCH_NOT_STARTED = 14
    const val MATCH_ALREADY_FINISHED = 15
    const val INVALID_POSITION = 16
    const val NOT_YOUR_TURN = 17
    const val WRONG_PASSWORD = 18
}

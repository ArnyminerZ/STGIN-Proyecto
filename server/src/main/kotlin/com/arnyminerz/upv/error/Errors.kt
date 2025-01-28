package com.arnyminerz.upv.error

import error.ErrorCodes
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

object Errors {
    @Serializable
    data object MissingCredentials : Error(ErrorCodes.MISSING_CREDENTIALS, "Missing credentials.", HttpStatusCode.Unauthorized)

    @Serializable
    data object InvalidCredentials : Error(ErrorCodes.INVALID_CREDENTIALS, "Invalid credentials.")

    @Serializable
    data object UserAlreadyExists : Error(ErrorCodes.USER_ALREADY_EXISTS, "User already exists.")

    @Serializable
    data object NotLoggedIn : Error(ErrorCodes.NOT_LOGGED_IN, "Not logged in.", HttpStatusCode.Forbidden)

    @Serializable
    data object MatchNotFound : Error(ErrorCodes.MATCH_NOT_FOUND, "Match not found.")

    @Serializable
    data object CannotMatchAgainstYourself : Error(ErrorCodes.CANNOT_MATCH_AGAINST_YOURSELF, "You cannot match against yourself.")

    @Serializable
    data object UserNotFound : Error(ErrorCodes.USER_NOT_FOUND, "User not found.")

    @Serializable
    data object MatchPending : Error(ErrorCodes.MATCH_PENDING, "Match pending.")

    @Serializable
    data object MatchNotReady : Error(ErrorCodes.MATCH_NOT_READY, "Match not ready.")

    @Serializable
    data object MatchAlreadyStarted : Error(ErrorCodes.MATCH_ALREADY_STARTED, "Match already started.")

    @Serializable
    data object PositionOutOfBounds : Error(ErrorCodes.POSITION_OUT_OF_BOUNDS, "Position out of bounds.")

    @Serializable
    data object ForbiddenPosition : Error(ErrorCodes.FORBIDDEN_POSITION, "Forbidden position.")

    @Serializable
    data object NotYourMatch : Error(ErrorCodes.NOT_YOUR_MATCH, "You are not in this match.", HttpStatusCode.Forbidden)

    @Serializable
    data object MatchNotStarted : Error(ErrorCodes.MATCH_NOT_STARTED, "Match not started.")

    @Serializable
    data object MatchAlreadyFinished : Error(ErrorCodes.MATCH_ALREADY_FINISHED, "Match already finished.")

    @Serializable
    data object InvalidPosition : Error(ErrorCodes.INVALID_POSITION, "Invalid position.")

    @Serializable
    data object NotYourTurn : Error(ErrorCodes.NOT_YOUR_TURN, "It's not your turn.", HttpStatusCode.Conflict)
}

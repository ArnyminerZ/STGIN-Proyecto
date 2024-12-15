package com.arnyminerz.upv.error

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

object Errors {
    @Serializable
    data object MissingCredentials : Error(1, "Missing credentials.", HttpStatusCode.Unauthorized)

    @Serializable
    data object InvalidCredentials : Error(2, "Invalid credentials.")

    @Serializable
    data object UserAlreadyExists : Error(3, "User already exists.")

    @Serializable
    data object NotLoggedIn : Error(4, "Not logged in.", HttpStatusCode.Forbidden)

    @Serializable
    data object MatchNotFound : Error(5, "Match not found.")

    @Serializable
    data object CannotMatchAgainstYourself : Error(6, "You cannot match against yourself.")

    @Serializable
    data object UserNotFound : Error(7, "User not found.")

    @Serializable
    data object MatchPending : Error(8, "Match pending.")

    @Serializable
    data object MatchNotReady : Error(9, "Match not ready.")

    @Serializable
    data object MatchAlreadyStarted : Error(10, "Match already started.")

    @Serializable
    data object PositionOutOfBounds : Error(11, "Position out of bounds.")

    @Serializable
    data object ForbiddenPosition : Error(12, "Forbidden position.")

    @Serializable
    data object NotYourMatch : Error(13, "You are not in this match.", HttpStatusCode.Forbidden)
}

package com.arnyminerz.upv.error

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

object Errors {
    @Serializable
    data object MissingCredentials: Error(1, "Missing credentials.", HttpStatusCode.Unauthorized)

    @Serializable
    data object InvalidCredentials: Error(2, "Invalid credentials.")

    @Serializable
    data object UserAlreadyExists: Error(3, "User already exists.")
}

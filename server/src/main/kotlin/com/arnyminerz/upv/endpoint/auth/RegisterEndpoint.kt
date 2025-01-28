package com.arnyminerz.upv.endpoint.auth

import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.User
import com.arnyminerz.upv.endpoint.type.Endpoint
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.error.Errors
import com.arnyminerz.upv.security.Passwords
import io.ktor.http.HttpMethod
import io.ktor.server.routing.RoutingContext
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Registers a new user in the database.
 */
object RegisterEndpoint : Endpoint(Endpoints.Auth.REGISTER, HttpMethod.Post) {
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun EndpointContext.body() {
        val (username, password) = fetchCredentials()

        // TODO: Add password safety checks
        if (username.length < 3 || password.length < 6) {
            respondFailure(Errors.InvalidCredentials)
        }

        // Check if the user already exists
        val userExists = ServerDatabase { User.findById(username) } != null
        if (userExists) {
            respondFailure(Errors.UserAlreadyExists)
        }

        val (salt, hash) = Passwords.hash(password)

        ServerDatabase {
            User.new(username) {
                this.salt = salt
                this.hash = hash
            }
        }

        respondSuccess()
    }
}

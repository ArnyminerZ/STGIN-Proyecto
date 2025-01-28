package backend

import Endpoints
import exception.ServerException
import exception.server.UserAlreadyExistsException
import exception.server.WrongPasswordException
import httpClient
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.isSuccess

object AuthBackend : Backend() {
    /**
     * Tries logging in with the given credential. If nothing throws, the login was successful.
     *
     * @throws WrongPasswordException If the password given doesn't match the user. This may also mean that the user
     * doesn't exist.
     * @throws ServerException If an unknown error is returned by the server.
     */
    suspend fun login(username: String, password: String) {
        val response = httpClient.post(Endpoints.Auth.LOGIN) {
            basicAuth(username, password)
        }
        errorHandler(response)
    }

    /**
     * Registers a new user with the given arguments.
     *
     * If no exception is thrown, the user was successfully registered.
     *
     * @param username The username for the user.
     * @param password The password to set for the user.
     *
     * @throws UserAlreadyExistsException If [username] is already registered.
     */
    suspend fun register(username: String, password: String) {
        val response = httpClient.post(Endpoints.Auth.REGISTER) {
            basicAuth(username, password)
        }
        errorHandler(response)
    }

    /**
     * Checks whether there's a user currently logged in.
     */
    suspend fun isLoggedIn(): Boolean {
        val response = httpClient.get(Endpoints.Game.MATCHES)
        return response.status.isSuccess()
    }
}

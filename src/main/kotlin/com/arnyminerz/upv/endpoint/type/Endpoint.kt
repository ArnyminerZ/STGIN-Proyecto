package com.arnyminerz.upv.endpoint.type

import com.arnyminerz.upv.endpoint.auth.RegisterEndpoint.respondFailure
import com.arnyminerz.upv.error.Error
import com.arnyminerz.upv.error.Errors
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.authorization
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.RoutingHandler
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

abstract class Endpoint(val route: String, val method: HttpMethod = HttpMethod.Get) {
    val handler: RoutingHandler = { invoke() }

    /**
     * Tries to extract the credentials from the Authorization header.
     * May halt the current execution if no valid credentials are given, failing with [Errors.MissingCredentials].
     */
    @ExperimentalEncodingApi
    protected suspend fun RoutingContext.basicCredentials(): Pair<String, String> {
        val auth = call.request.authorization()
        if (auth == null) {
            respondFailure(Errors.MissingCredentials)
        }

        val (username, password) = Base64
            .decode(auth!!.removePrefix("Basic "))
            .decodeToString()
            .split(':', limit = 2)
        return username to password
    }

    /**
     * Thrown by methods inside of [body] to indicate that the request has already been handled, and the flow should not
     * continue.
     */
    class RequestHandledException(message: String? = null) : Exception("Request already handled. Message: $message")

    /**
     * Handles and responds to a failure case by sending an error response and terminating the flow.
     *
     * @param error The error object containing details about the failure, including its HTTP status code, error code,
     * and message to be included in the response.
     */
    protected suspend inline fun RoutingContext.respondFailure(error: Error) {
        call.respond(error.httpStatusCode, error)
        throw RequestHandledException("Request failed with error code #${error.code}")
    }

    protected suspend inline fun RoutingContext.respondSuccess() {
        call.respond(HttpStatusCode.OK, "OK")
        throw RequestHandledException("Request succeeded.")
    }

    suspend operator fun RoutingContext.invoke() {
        try {
            body()
        } catch (_: RequestHandledException) {
            // pass
        }
    }

    /**
     * Defines the logic for handling the incoming HTTP request. This method is intended to be implemented
     * to process the request and optionally return a response.
     *
     * The method operates on the current `RoutingContext`, providing access to the request and response
     * objects. It can utilize helper methods such as `respondFailure` for sending error responses.
     *
     * Implementations can throw the [RequestHandledException] to indicate that the request has already
     * been handled and further processing should stop.
     *
     * This method is called automatically when the route is triggered and should handle any necessary
     * operations, including responding to the client if applicable.
     *
     * Usage of this method in subclasses must ensure that any required responses are sent or exceptions
     * properly managed to avoid leaving the request hanging.
     */
    protected abstract suspend fun RoutingContext.body()
}

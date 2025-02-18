package com.arnyminerz.upv.endpoint.type

import com.arnyminerz.upv.error.Error
import com.arnyminerz.upv.error.Errors
import com.arnyminerz.upv.plugins.json
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.ParametersBuilder
import io.ktor.http.URLBuilder
import io.ktor.server.request.authorization
import io.ktor.server.request.contentType
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.RoutingHandler
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.SerializationStrategy

abstract class Endpoint(val route: String, val method: HttpMethod = HttpMethod.Get) {
    val handler: RoutingHandler = { invoke() }

    /**
     * Tries to extract the credentials from the Authorization header.
     * May halt the current execution if no valid credentials are given, failing with [Errors.MissingCredentials].
     */
    @ExperimentalEncodingApi
    protected suspend fun EndpointContext.fetchCredentials(): Pair<String, String> {
        val auth = call.request.authorization()

        val formUsername = formParameters["username"]
        val formPassword = formParameters["password"]

        if (auth == null && formUsername.isNullOrBlank() && formPassword.isNullOrBlank()) {
            respondFailure(Errors.MissingCredentials)
        }

        val (username, password) = if (!auth.isNullOrBlank()) {
            Base64
                .decode(auth.removePrefix("Basic "))
                .decodeToString()
                .split(':', limit = 2)
                .let { it.first() to it.last() }
        } else {
            formUsername!! to formPassword!!
        }
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
    protected suspend inline fun EndpointContext.respondFailure(error: Error) {
        val redirectTo = formParameters["redirectTo"]

        if (redirectTo != null) {
            call.respondRedirect(
                URLBuilder(call.request.headers[HttpHeaders.Referrer] ?: redirectTo)
                    .apply {
                        parameters["error"] = error.code.toString()
                    }
                    .build()
            )
        } else {
            call.respond(error.httpStatusCode, error)
        }
        throw RequestHandledException("Request failed with error code #${error.code}")
    }

    protected suspend inline fun EndpointContext.respondSuccess() {
        val redirectTo = formParameters["redirectTo"]

        if (redirectTo != null) {
            call.respondRedirect(redirectTo)
        } else {
            call.respondText("OK", status = HttpStatusCode.OK)
        }
        throw RequestHandledException("Request succeeded.")
    }

    protected suspend inline fun EndpointContext.respondSuccess(
        text: String,
        statusCode: HttpStatusCode = HttpStatusCode.OK
    ) {
        call.respondText(text, status = statusCode)
        throw RequestHandledException("Request succeeded.")
    }

    protected suspend inline fun <Type: Any> EndpointContext.respondSuccess(
        body: Type,
        serializer: SerializationStrategy<Type>,
        statusCode: HttpStatusCode = HttpStatusCode.OK
    ) {
        val jsonBody  = json.encodeToString(serializer, body)
        call.respondText(jsonBody, ContentType.Application.Json, statusCode)

        throw RequestHandledException("Request succeeded.")
    }

    suspend operator fun RoutingContext.invoke() {
        try {
            val formParameters = if (call.request.contentType() == ContentType.Application.FormUrlEncoded) {
                call.receiveParameters()
            } else {
                ParametersBuilder(0).build()
            }
            val context = EndpointContext(call, formParameters)
            context.body()
        } catch (_: RequestHandledException) {
            // pass
        }
    }

    /**
     * Defines the logic for handling the incoming HTTP request. This method is intended to be implemented
     * to process the request and optionally return a response.
     *
     * The method operates on the current `EndpointContext`, providing access to the request and response
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
    protected abstract suspend fun EndpointContext.body()
}

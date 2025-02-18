package com.arnyminerz.upv.endpoint.auth

import Endpoints
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.endpoint.type.SecureEndpoint
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * Checks whether the user is logged in or not.
 */
object SessionEndpoint : SecureEndpoint(Endpoints.Auth.SESSION, HttpMethod.Get) {
    override suspend fun EndpointContext.secureBody(userId: String) {
        respondSuccess(userId, HttpStatusCode.OK)
    }
}

package com.arnyminerz.upv.endpoint.auth

import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.endpoint.type.SecureEndpoint
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * Checks whether the user is logged in or not.
 */
object SessionEndpoint : SecureEndpoint("/api/auth/session", HttpMethod.Get) {
    override suspend fun EndpointContext.secureBody(userId: String) {
        respondSuccess(userId, HttpStatusCode.OK)
    }
}

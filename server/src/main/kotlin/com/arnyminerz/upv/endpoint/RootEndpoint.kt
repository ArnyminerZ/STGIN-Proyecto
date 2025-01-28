package com.arnyminerz.upv.endpoint

import com.arnyminerz.upv.endpoint.type.Endpoint
import com.arnyminerz.upv.endpoint.type.EndpointContext

object RootEndpoint : Endpoint(Endpoints.ROOT) {
    override suspend fun EndpointContext.body() {
        respondSuccess()
    }
}

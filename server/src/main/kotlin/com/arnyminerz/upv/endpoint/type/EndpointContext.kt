package com.arnyminerz.upv.endpoint.type

import io.ktor.http.Parameters
import io.ktor.server.routing.RoutingCall

class EndpointContext(
    val call: RoutingCall,
    val formParameters: Parameters
)

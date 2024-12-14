package com.arnyminerz.upv.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("CORS")

fun Application.installCORS() {
    install(CORS) {
        val host = System.getenv("CORS_HOST")
        if (host != null) {
            allowHost(host, schemes = listOf("http", "https"))

            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Patch)
            allowMethod(HttpMethod.Delete)

            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.ContentLength)
        } else {
            logger.warn("Allowing all CORS hosts. This is not recommended for production environments.")
            anyHost()
            anyMethod()
        }
    }
}

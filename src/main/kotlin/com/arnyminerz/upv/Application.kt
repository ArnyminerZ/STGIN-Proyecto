package com.arnyminerz.upv

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.tomcat.jakarta.Tomcat

fun main() {
    embeddedServer(Tomcat, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
}

package com.arnyminerz.upv

import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.plugins.configureRouting
import com.arnyminerz.upv.plugins.installContentNegotiation
import com.arnyminerz.upv.plugins.installSessions
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.tomcat.jakarta.Tomcat

fun main() {
    ServerDatabase.initialize()

    embeddedServer(Tomcat, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    installContentNegotiation()
    configureRouting()
    installSessions()
}

package com.arnyminerz.upv

import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.plugins.configureRouting
import com.arnyminerz.upv.plugins.installCORS
import com.arnyminerz.upv.plugins.installContentNegotiation
import com.arnyminerz.upv.plugins.installSessions
import com.arnyminerz.upv.plugins.installWebSockets
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.tomcat.jakarta.Tomcat
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking { ServerDatabase.initialize() }

    embeddedServer(Tomcat, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    installContentNegotiation()
    installCORS()
    installSessions()
    installWebSockets()

    configureRouting()
}

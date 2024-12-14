package com.arnyminerz.upv.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

val json = Json {
    isLenient = true
}

fun Application.installContentNegotiation() {
    install(ContentNegotiation) {
        json(json)
    }
}

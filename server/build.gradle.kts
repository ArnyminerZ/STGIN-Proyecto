plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktor)
}

group = "com.arnyminerz.upv.stgin"
version = "0.0.1"

application {
    mainClass.set("com.arnyminerz.upv.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":shared"))

    // Ktor
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.tomcat)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.serialization.json)

    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.javatime)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.json)
    implementation(libs.h2)
    implementation(libs.postgresql)

    // KotlinX Serialization
    implementation(libs.kotlinx.serialization)

    // Logging
    implementation(libs.logback)

    // Redis Client
    implementation(libs.kreds)

    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

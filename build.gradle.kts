plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktor)
}

group = "com.arnyminerz.upv"
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
    // Ktor
    implementation(libs.ktor.contentNegotiation)
    implementation(libs.ktor.core)
    implementation(libs.ktor.cors)
    implementation(libs.ktor.tomcat)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.sessions)

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

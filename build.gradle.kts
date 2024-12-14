plugins {
    alias(libs.plugins.jvm)
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
    implementation(libs.ktor.core)
    implementation(libs.ktor.tomcat)

    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation(libs.postgresql)

    implementation(libs.logback)
}

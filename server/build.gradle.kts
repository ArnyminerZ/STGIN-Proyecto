import kotlin.io.path.div
import kotlin.io.path.readLines

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

val srcDir = File(projectDir, "src")
val mainDir = File(srcDir, "main")
val resourcesDir = File(mainDir, "resources")
val webDir = File(resourcesDir, "web")
val generatedJsDir = File(webDir, "js_gen")
    .also { it.mkdirs() }

sourceSets {
    main {
        resources.srcDir(generatedJsDir.parentFile)
    }
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

val generateErrorCodes = tasks.register("generateErrorCodes") {
    val file = rootProject.rootDir.toPath() / "shared" / "src" / "commonMain" / "kotlin" / "error" / "ErrorCodes.kt"
    val lines = file.readLines()
        .map { it.trim() }
        .filter { it.startsWith("const val") }
        .map { line ->
            "export " + line.replace("val ", "")
        }
    File(generatedJsDir, "error_codes.mjs").let { outputFile ->
        if (outputFile.exists()) outputFile.delete()
        outputFile.outputStream().bufferedWriter().use { output ->
            output.write(lines.joinToString("\n"))
        }
        println("Written error codes module to $outputFile")
    }
}

tasks.named("processResources") {
    dependsOn(generateErrorCodes)
}

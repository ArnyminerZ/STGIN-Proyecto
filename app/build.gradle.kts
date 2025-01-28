import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

val configDir = layout.buildDirectory.dir("config").get().apply {
    if (!asFile.exists()) {
        if (!asFile.mkdirs()) {
            error("Could not create config directory ($asFile)")
        } else {
            println("Created config directory ($asFile)")
        }
    }
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "app"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "webApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                    port = 3000
                }
                configDirectory = File(project.rootDir, "webpack.config.d")
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain {
            resources.srcDir(configDir.asFile)

            dependencies {
                // Shared code
                implementation(project(":shared"))

                // Base Compose Dependencies
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.runtime)

                // Compose Navigation
                implementation(libs.compose.navigation)

                // ViewModels
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)

                // Kotlinx Coroutines & Serialization
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization)

                implementation(libs.napier)

                // Ktor Client
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.js)
                implementation(libs.ktor.serialization.json)
            }
        }
    }
}

val createConfigTask = task("createConfig") {
    val configFile = configDir.file("config.js").asFile

    // Delete config file if it already exists
    if (configFile.exists()) configFile.delete()

    val serverUrl = System.getenv("SERVER_URL") ?: "http://localhost:8080"
    println("Server URL: $serverUrl")
    configFile.outputStream().bufferedWriter().use { writer ->
        writer.write("""
            const SERVER_URL = "$serverUrl";
        """.trimIndent())
    }
}

tasks.named("compileKotlinWasmJs") {
    dependsOn(createConfigTask)
}

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs()

    jvm()

    sourceSets {
        commonMain {
            dependencies {
                // Kotlinx Serialization
                implementation(libs.kotlinx.serialization)
            }
        }
    }
}

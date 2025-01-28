import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

@OptIn(ExperimentalWasmDsl::class)
kotlin {
    wasmJs {
        browser()
    }
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

plugins {
    // Shared
    alias(libs.plugins.kotlinx.serialization) apply false

    // For app
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false

    // For server
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.ktor) apply false
}

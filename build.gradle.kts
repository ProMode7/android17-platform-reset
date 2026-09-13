// Top-level build file. Per-module configuration lives in each module's build.gradle.kts.
//
// AGP 9's "built-in Kotlin" compiles Kotlin sources itself rather than through the standalone
// org.jetbrains.kotlin.android plugin. It defaults to an older Kotlin Gradle Plugin version
// internally unless a newer one is declared explicitly here -- pin it to match the Compose
// compiler plugin version below, matching the pattern already verified across the five source
// repos this project consolidates. See https://kotl.in/gradle/agp-built-in-kotlin
buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}

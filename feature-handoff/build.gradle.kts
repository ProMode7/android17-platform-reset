plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "dev.pramodpatel.handoff"
    compileSdk = 37

    defaultConfig {
        // The platform Handoff surface (android.app.HandoffActivityData et al.) only exists on
        // API 37, but this module's own minSdk mirrors the app-wide consolidated choice of 26 (see
        // root README "minSdk choice"): the platform calls that touch these classes live in
        // :app's MainActivity, gated implicitly by the OS only ever invoking
        // onHandoffActivityDataRequested on a real API 37 device.
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core-design"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)

    testImplementation(libs.junit)
}

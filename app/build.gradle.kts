plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.pramodpatel.platformreset"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.pramodpatel.platformreset"
        // The least restrictive minSdk that still supports every consolidated feature: four of
        // the five source repos (AppFunctions, Adaptive Layouts, NPU, Security Hardening) already
        // used minSdk 26; only the standalone Handoff repo used minSdk 37, because Handoff was
        // ALL that app did. Here, the platform Handoff calls (setHandoffEnabled,
        // onHandoffActivityDataRequested) live in MainActivity and are only ever invoked by an
        // API 37 OS in practice -- see the root README "minSdk choice" for the full reasoning.
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":core-design"))
    implementation(project(":feature-appfunctions"))
    implementation(project(":feature-adaptive"))
    implementation(project(":feature-handoff"))
    implementation(project(":feature-npu"))
    implementation(project(":feature-security"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.navigation.compose)

    // The real androidx.window / Compose Material 3 adaptive API this app dogfoods at the home
    // dashboard level -- same dependency set as :feature-adaptive.
    implementation(libs.androidx.window.core)
    implementation(libs.androidx.window)
    implementation(libs.androidx.material3.adaptive)
    implementation(libs.androidx.material3.adaptive.layout)
    implementation(libs.androidx.material3.adaptive.navigation)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}

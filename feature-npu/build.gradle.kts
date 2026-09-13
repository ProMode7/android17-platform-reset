import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.promode.npu"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    // The bundled .tflite model must not be compressed by the APK packager, or TFLite's
    // mmap-based loader (FileChannel.map) will fail at runtime.
    androidResources {
        noCompress += "tflite"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = false
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core-design"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // LiteRT (formerly TensorFlow Lite). litert-api alone crashes at runtime (SIGABRT on
    // Environment.nativeCreate(), which dlopen()s libLiteRt.so, shipped only in the umbrella
    // `litert` AAR); that AAR can't be added directly either, since both artifacts declare the
    // same manifest namespace and AGP's merger rejects that. Fixed the same way the source repo
    // fixed it: the two native libraries are vendored directly under src/main/jniLibs/, extracted
    // from the litert:2.2.0 AAR. See README "Real bugs found and fixed."
    implementation(libs.litert.api)

    debugImplementation(libs.androidx.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

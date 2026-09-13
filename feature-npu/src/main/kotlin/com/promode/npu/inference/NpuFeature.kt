package com.promode.npu.inference

import android.content.Context
import android.content.pm.PackageManager

/**
 * Wraps the Android 17 (API 37) NPU device-feature check.
 *
 * Confirmed directly against the API 37 platform SDK shipped in this project's
 * toolchain (`$ANDROID_HOME/platforms/android-37.0/`):
 *
 *  - `data/features.txt` lists the literal device feature string `android.hardware.npu`.
 *  - `data/api-versions.xml` shows `PackageManager.FEATURE_NEURAL_PROCESSING_UNIT` was
 *    added `since="37.0"`.
 *
 * [PackageManager.FEATURE_NEURAL_PROCESSING_UNIT] is a compile-time String constant, so
 * referencing it directly is safe even though this module's minSdk (26) is well below
 * 37: javac/kotlinc inline the literal value into the call site, and
 * [PackageManager.hasSystemFeature] simply returns `false` for a feature string the
 * running platform doesn't recognize. No reflection or SDK_INT gating is required.
 */
object NpuFeature {

    /** The literal feature string backing [PackageManager.FEATURE_NEURAL_PROCESSING_UNIT]. */
    const val FEATURE_NPU = "android.hardware.npu"

    /**
     * True if the running device has advertised NPU hardware to the platform. This is
     * the OS-level signal the `<uses-feature android:name="android.hardware.npu"
     * android:required="false" />` manifest entry pairs with; it says nothing on its
     * own about whether LiteRT can actually schedule work onto that NPU (see
     * [com.google.ai.edge.litert.Environment.getAvailableAccelerators] for that).
     */
    fun isDeclaredByPlatform(context: Context): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_NEURAL_PROCESSING_UNIT)
}

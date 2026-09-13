package com.pramodpatel.platformreset

/** One row on the home dashboard / tablet rail, and one navigation destination. */
data class FeatureItem(
    val id: String,
    val title: String,
    val subtitle: String,
)

/**
 * The five features this app consolidates, in the exact order and wording of the approved
 * Main.dc.html / TabletHome.dc.html mockups.
 */
object FeatureCatalog {
    const val APPFUNCTIONS = "appfunctions"
    const val ADAPTIVE = "adaptive"
    const val HANDOFF = "handoff"
    const val NPU = "npu"
    const val SECURITY = "security"

    val items: List<FeatureItem> = listOf(
        FeatureItem(APPFUNCTIONS, "AppFunctions", "androidx.appfunctions · alpha11"),
        FeatureItem(ADAPTIVE, "Adaptive Layouts", "androidx.window · verified on Pixel Tablet"),
        FeatureItem(HANDOFF, "Handoff API", "Activity.setHandoffEnabled · real API, verified"),
        FeatureItem(NPU, "NPU + Inference", "LiteRT · Accelerator.NPU → GPU → CPU"),
        FeatureItem(SECURITY, "Security Hardening", "4 checks · SMS · LAN · TLS · Bluetooth"),
    )
}

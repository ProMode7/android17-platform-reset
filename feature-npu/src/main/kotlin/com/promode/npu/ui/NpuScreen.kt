package com.promode.npu.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.promode.npu.inference.NpuFeature
import com.pramodpatel.platformreset.design.CodeSnippetBlock
import com.pramodpatel.platformreset.design.DetailBody
import com.pramodpatel.platformreset.design.DetailBreadcrumb
import com.pramodpatel.platformreset.design.DetailHeadline
import com.pramodpatel.platformreset.design.VerifiedRow
import com.pramodpatel.platformreset.design.VerifiedSectionLabel

/**
 * The NPU + Inference feature screen: on-device MobileNet V1 classification via LiteRT's
 * accelerator-aware API, ported from the standalone `android17-npu-permission` repo's
 * `MainActivity`/`ClassifierScreen`. The real classify button is wired to the same
 * [ClassifierViewModel] and bundled model/sample image as that repo.
 */
@Composable
fun NpuScreen(onBack: (() -> Unit)? = null) {
    val context = LocalContext.current
    val viewModel: ClassifierViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        if (onBack != null) {
            DetailBreadcrumb(breadcrumb = "Android 17 / NPU + Inference", onBack = onBack)
        }
        DetailHeadline(
            title = "NPU + Inference",
            versionLine = "com.google.ai.edge.litert:litert-api:2.2.0 · API 37+",
        )
        DetailBody(
            "Android 17 added PackageManager.FEATURE_NEURAL_PROCESSING_UNIT " +
                "(android.hardware.npu). This screen classifies a bundled sample image with a " +
                "quantized MobileNet V1 model, preferring the NPU, falling back to GPU, and " +
                "finally CPU, using LiteRT's Environment/CompiledModel/Accelerator API.",
        )
        CodeSnippetBlock(
            code = "<uses-feature\n    android:name=\"android.hardware.npu\"\n" +
                "    android:required=\"false\" />",
        )
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            VerifiedSectionLabel()
            VerifiedRow("android.hardware.npu confirmed in the real android-37.0 features.txt / api-versions.xml")
            VerifiedRow("Environment / CompiledModel / Accelerator confirmed by decompiling litert-api-2.2.0.aar")
            VerifiedRow("Classifies the bundled Grace Hopper photo as \"military uniform: 76%\" on a real API 37 emulator")
        }

        VerifiedSectionLabel(text = "Live demo")
        Text(
            text = "uses-feature ${NpuFeature.FEATURE_NPU} (required=false)\n" +
                "PackageManager reports NPU on this device: ${viewModel.npuDeclaredByPlatform}",
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(
            onClick = {
                val bitmap = context.assets.open("sample_input.jpg").use(BitmapFactory::decodeStream)
                viewModel.classify(bitmap)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Classify bundled sample image")
        }
        when (val s = state) {
            is ClassifierUiState.Idle -> Text("Idle.")
            is ClassifierUiState.Running -> Text("Running inference...")
            is ClassifierUiState.Done -> {
                Text("Requested accelerator: ${s.outcome.acceleratorRequested}")
                Text("Accelerator actually used: ${s.outcome.acceleratorActuallyUsed}")
                s.outcome.topResults.forEach {
                    Text("${it.label}: ${(it.confidence * 100).toInt()}%")
                }
            }
            is ClassifierUiState.Failed -> Text("Inference failed: ${s.message}")
        }
    }
}

package com.promode.npu.inference

import android.content.Context
import android.graphics.Bitmap
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel
import com.google.ai.edge.litert.Environment
import com.google.ai.edge.litert.LiteRtException
import java.io.Closeable

private const val MODEL_ASSET = "mobilenet_v1_1.0_224_quant.tflite"
private const val LABELS_ASSET = "labels.txt"
private const val INPUT_SIZE = 224

/**
 * Runs the bundled quantized MobileNet V1 image classifier through LiteRT's
 * accelerator-aware `Environment` / `CompiledModel` API
 * (`com.google.ai.edge.litert`, from the `litert-api` artifact, confirmed present by
 * inspecting `litert-api-2.2.0.aar`'s `classes.jar`).
 *
 * Accelerator selection:
 *  1. Ask [Environment.getAvailableAccelerators] what LiteRT can actually dispatch to
 *     on this device.
 *  2. Run that set through [DelegateSelector] to get a single preferred accelerator
 *     (NPU > GPU > CPU).
 *  3. Try to compile the model for that accelerator. If compilation or the first run
 *     throws [LiteRtException] -- e.g. an NPU was reported available but its runtime
 *     library isn't actually ready yet, or a GPU delegate can't service this model --
 *     retry once, forcing CPU. CPU is treated as always viable.
 *
 * See the project README's "Current limitations" section for exactly what is and is
 * not independently verified about step 3 actually landing work on NPU silicon versus
 * silently falling through to CPU inside LiteRT itself.
 */
class ImageClassifier(private val context: Context) : Closeable {

    private val labels: List<String> by lazy {
        context.assets.open(LABELS_ASSET).bufferedReader().use { it.readLines() }
    }

    private var environment: Environment? = null

    fun classify(bitmap: Bitmap, topK: Int = 3): ClassificationOutcome {
        val env = environment ?: Environment.create(context).also { environment = it }

        val available = try {
            env.getAvailableAccelerators()
        } catch (e: LiteRtException) {
            setOf(Accelerator.CPU)
        }
        val requested = DelegateSelector.select(available)

        val (model, actuallyUsed) = compile(env, requested)
        try {
            val input = preprocess(bitmap)
            val inputBuffers = model.createInputBuffers()
            val outputBuffers = model.createOutputBuffers()
            try {
                inputBuffers[0].writeInt8(input)
                model.run(inputBuffers, outputBuffers)
                val raw = outputBuffers[0].readInt8()
                val results = ClassificationPostprocessor.topK(raw, labels, topK)
                return ClassificationOutcome(results, requested, actuallyUsed)
            } finally {
                inputBuffers.forEach { it.close() }
                outputBuffers.forEach { it.close() }
            }
        } finally {
            model.close()
        }
    }

    private fun compile(env: Environment, requested: Accelerator): Pair<CompiledModel, Accelerator> {
        if (requested != Accelerator.CPU) {
            try {
                val model = CompiledModel.create(
                    context.assets,
                    MODEL_ASSET,
                    CompiledModel.Options(requested),
                    env,
                )
                return model to requested
            } catch (e: LiteRtException) {
                // Fall through to the CPU path below.
            }
        }
        val cpuModel = CompiledModel.create(
            context.assets,
            MODEL_ASSET,
            CompiledModel.Options(Accelerator.CPU),
            env,
        )
        return cpuModel to Accelerator.CPU
    }

    /**
     * Converts an ARGB [Bitmap] into the raw uint8 RGB byte layout the quantized
     * MobileNet input tensor expects: `224x224x3`, no normalization (the quant model
     * consumes raw 0..255 pixel values directly).
     */
    private fun preprocess(bitmap: Bitmap): ByteArray {
        val scaled = if (bitmap.width == INPUT_SIZE && bitmap.height == INPUT_SIZE) {
            bitmap
        } else {
            Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        }
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        scaled.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        val bytes = ByteArray(INPUT_SIZE * INPUT_SIZE * 3)
        var i = 0
        for (pixel in pixels) {
            bytes[i++] = ((pixel shr 16) and 0xFF).toByte() // R
            bytes[i++] = ((pixel shr 8) and 0xFF).toByte()  // G
            bytes[i++] = (pixel and 0xFF).toByte()          // B
        }
        return bytes
    }

    override fun close() {
        environment?.close()
        environment = null
    }
}

package com.promode.npu.inference

import com.google.ai.edge.litert.Accelerator

/** One labeled prediction, plus which accelerator produced it. */
data class Classification(
    val label: String,
    val confidence: Float,
)

data class ClassificationOutcome(
    val topResults: List<Classification>,
    val acceleratorRequested: Accelerator,
    val acceleratorActuallyUsed: Accelerator,
)

/**
 * Turns a raw quantized model output tensor into ranked, human-readable predictions.
 *
 * This is pure Kotlin -- no Android, no LiteRT runtime calls -- so it is exercised by a
 * plain JVM unit test against a synthetic output array
 * (see `ClassificationPostprocessorTest`), independent of whether an interpreter can
 * actually be instantiated on the host running the test.
 */
object ClassificationPostprocessor {

    /**
     * [rawOutput] is the model's quantized uint8 output tensor, read back as a signed
     * `ByteArray` (LiteRT's [com.google.ai.edge.litert.TensorBuffer.readInt8] returns
     * the raw byte pattern; each element must be masked with `0xFF` to recover the
     * intended 0..255 unsigned quantized value). [labels] must have exactly one entry
     * per output class -- for the bundled MobileNet V1 model that is 1001 entries
     * (1000 ImageNet classes plus a "background" class at index 0).
     *
     * Throws [IllegalArgumentException] if the sizes don't match, since a shape
     * mismatch between a model's output tensor and its label file is a real bug, not
     * something to silently paper over.
     */
    fun topK(rawOutput: ByteArray, labels: List<String>, k: Int = 3): List<Classification> {
        require(rawOutput.size == labels.size) {
            "Output tensor has ${rawOutput.size} entries but ${labels.size} labels were supplied"
        }
        require(k > 0) { "k must be positive, was $k" }

        return rawOutput.indices
            .map { index ->
                val quantized = rawOutput[index].toInt() and 0xFF
                Classification(label = labels[index], confidence = quantized / 255f)
            }
            .sortedByDescending { it.confidence }
            .take(k)
    }
}

package com.promode.npu.inference

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Sanity-checks the assets this app ships and that [ImageClassifier] loads at runtime,
 * without needing an Android runtime: plain [java.io.File] reads of
 * `app/src/main/assets/`, which the Gradle `Test` task runs with the module directory
 * as its working directory.
 *
 * This does not run the model -- doing that requires LiteRT's native `.so`, which only
 * ships for Android ABIs (see README "Current limitations") -- but it does verify the
 * two assumptions [ImageClassifier] and [ClassificationPostprocessor] depend on: that
 * the bundled file really is a TFLite FlatBuffer, and that the label file's shape
 * matches the 1001-class output the bundled MobileNet V1 model produces.
 */
class BundledAssetsTest {

    private fun assetsDir(): File {
        val candidates = listOf(
            File("src/main/assets"),
            File("app/src/main/assets"),
        )
        return candidates.firstOrNull { it.isDirectory } ?: error(
            "Could not locate src/main/assets from working dir ${File(".").absolutePath}"
        )
    }

    @Test
    fun `bundled model file is a valid TFLite FlatBuffer`() {
        val model = File(assetsDir(), "mobilenet_v1_1.0_224_quant.tflite")
        assertTrue("model asset is missing: $model", model.isFile)

        val header = model.inputStream().use { it.readNBytes(8) }
        // TFLite FlatBuffers store the 4-byte file identifier "TFL3" at offset 4.
        val identifier = String(header, 4, 4, Charsets.US_ASCII)
        assertEquals("TFL3", identifier)
    }

    @Test
    fun `label file has exactly one entry per MobileNet V1 output class`() {
        val labels = File(assetsDir(), "labels.txt")
        assertTrue("labels asset is missing: $labels", labels.isFile)

        val lines = labels.readLines().filter { it.isNotBlank() }
        // 1000 ImageNet classes plus the reserved "background" class at index 0.
        assertEquals(1001, lines.size)
        assertEquals("background", lines.first())
    }

    @Test
    fun `sample input image asset is present and non empty`() {
        val sample = File(assetsDir(), "sample_input.jpg")
        assertTrue("sample input asset is missing: $sample", sample.isFile)
        assertTrue(sample.length() > 0)
    }
}

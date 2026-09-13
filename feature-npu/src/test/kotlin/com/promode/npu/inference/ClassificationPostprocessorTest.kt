package com.promode.npu.inference

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ClassificationPostprocessorTest {

    @Test
    fun `ranks the highest quantized score first`() {
        val labels = listOf("background", "cat", "dog")
        val output = byteArrayOf(10, 200.toByte(), 100) // unsigned: 10, 200, 100

        val results = ClassificationPostprocessor.topK(output, labels, k = 3)

        assertEquals(listOf("cat", "dog", "background"), results.map { it.label })
    }

    @Test
    fun `confidence is the quantized byte scaled to 0 to 1`() {
        val labels = listOf("a", "b")
        val output = byteArrayOf(255.toByte(), 0)

        val results = ClassificationPostprocessor.topK(output, labels, k = 2)

        assertEquals(1.0f, results[0].confidence, 1e-6f)
        assertEquals(0.0f, results[1].confidence, 1e-6f)
    }

    @Test
    fun `respects k smaller than the number of classes`() {
        val labels = listOf("a", "b", "c", "d")
        val output = byteArrayOf(1, 4, 3, 2)

        val results = ClassificationPostprocessor.topK(output, labels, k = 2)

        assertEquals(2, results.size)
        assertEquals("b", results[0].label)
        assertEquals("c", results[1].label)
    }

    @Test
    fun `treats output bytes as unsigned, not signed, quantized values`() {
        // 0xFF as a signed Kotlin Byte is -1; it must be read back as 255 (max confidence).
        val labels = listOf("only")
        val output = byteArrayOf(0xFF.toByte())

        val results = ClassificationPostprocessor.topK(output, labels, k = 1)

        assertEquals(1.0f, results[0].confidence, 1e-6f)
    }

    @Test
    fun `rejects a shape mismatch between the output tensor and the label list`() {
        val labels = listOf("a", "b", "c")
        val output = byteArrayOf(1, 2)

        assertThrows(IllegalArgumentException::class.java) {
            ClassificationPostprocessor.topK(output, labels)
        }
    }

    @Test
    fun `rejects a non positive k`() {
        val labels = listOf("a")
        val output = byteArrayOf(1)

        assertThrows(IllegalArgumentException::class.java) {
            ClassificationPostprocessor.topK(output, labels, k = 0)
        }
    }
}

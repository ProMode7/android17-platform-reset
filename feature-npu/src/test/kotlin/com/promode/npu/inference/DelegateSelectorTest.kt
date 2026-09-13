package com.promode.npu.inference

import com.google.ai.edge.litert.Accelerator
import org.junit.Assert.assertEquals
import org.junit.Test

class DelegateSelectorTest {

    @Test
    fun `prefers NPU when all three accelerators are available`() {
        val available = setOf(Accelerator.CPU, Accelerator.GPU, Accelerator.NPU)
        assertEquals(Accelerator.NPU, DelegateSelector.select(available))
    }

    @Test
    fun `falls back to GPU when NPU is unavailable`() {
        val available = setOf(Accelerator.CPU, Accelerator.GPU)
        assertEquals(Accelerator.GPU, DelegateSelector.select(available))
    }

    @Test
    fun `falls back to CPU when only CPU is available`() {
        val available = setOf(Accelerator.CPU)
        assertEquals(Accelerator.CPU, DelegateSelector.select(available))
    }

    @Test
    fun `falls back to CPU when the reported set is empty`() {
        assertEquals(Accelerator.CPU, DelegateSelector.select(emptySet()))
    }

    @Test
    fun `falls back to CPU when the reported set only contains NONE`() {
        assertEquals(Accelerator.CPU, DelegateSelector.select(setOf(Accelerator.NONE)))
    }

    @Test
    fun `NPU present but GPU absent still selects NPU`() {
        val available = setOf(Accelerator.NPU, Accelerator.CPU)
        assertEquals(Accelerator.NPU, DelegateSelector.select(available))
    }

    @Test
    fun `order of the input set does not affect the outcome`() {
        val available = setOf(Accelerator.GPU, Accelerator.NPU, Accelerator.CPU, Accelerator.NONE)
        assertEquals(Accelerator.NPU, DelegateSelector.select(available))
    }
}

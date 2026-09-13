package com.promode.npu.inference

import com.google.ai.edge.litert.Accelerator

/**
 * Pure accelerator-selection policy, deliberately kept free of any Android or LiteRT
 * runtime calls so it is exercised directly by a plain JVM unit test
 * (see `DelegateSelectorTest`).
 *
 * [com.google.ai.edge.litert.Environment.getAvailableAccelerators] returns the set of
 * accelerators LiteRT itself believes it can dispatch to on this device -- this is the
 * real, current signal for "can I actually use the NPU right now", as distinct from
 * [NpuFeature.isDeclaredByPlatform], which only reflects the static manifest/platform
 * feature flag. [select] turns that set into a single preferred target using a fixed
 * priority: NPU, then GPU, then CPU. CPU is treated as always usable -- LiteRT can
 * always fall back to its XNNPACK CPU kernels -- so [select] never returns something
 * absent from a non-empty preference chain and never throws.
 */
object DelegateSelector {

    /** Priority order used when more than one accelerator is available. */
    val PRIORITY: List<Accelerator> = listOf(Accelerator.NPU, Accelerator.GPU, Accelerator.CPU)

    /**
     * Picks the highest-priority accelerator present in [available]. Falls back to
     * [Accelerator.CPU] when [available] contains neither NPU nor GPU (including when
     * it is empty, or only reports [Accelerator.NONE]) -- CPU execution is always the
     * safe universal fallback for a LiteRT model.
     */
    fun select(available: Set<Accelerator>): Accelerator =
        PRIORITY.firstOrNull { it in available } ?: Accelerator.CPU
}

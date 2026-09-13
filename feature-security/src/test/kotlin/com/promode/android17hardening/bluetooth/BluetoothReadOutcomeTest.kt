package com.promode.android17hardening.bluetooth

import org.junit.Assert.assertEquals
import org.junit.Test

class BluetoothReadOutcomeTest {

    @Test
    fun `positive byte count is DataRead`() {
        assertEquals(BluetoothReadOutcome.DataRead(42), interpretRfcommRead(42))
    }

    @Test
    fun `minus one is EndOfStream, the Android 17 RFCOMM disconnect signal`() {
        assertEquals(BluetoothReadOutcome.EndOfStream, interpretRfcommRead(-1))
    }

    @Test
    fun `zero is reported distinctly, not folded into DataRead or EndOfStream`() {
        assertEquals(BluetoothReadOutcome.ZeroByteRead, interpretRfcommRead(0))
    }

    @Test
    fun `values below minus one are reported as unexpected, never silently accepted`() {
        assertEquals(
            BluetoothReadOutcome.UnexpectedNegativeValue(-2),
            interpretRfcommRead(-2)
        )
        assertEquals(
            BluetoothReadOutcome.UnexpectedNegativeValue(-100),
            interpretRfcommRead(-100)
        )
    }

    @Test
    fun `minus one is never mistaken for a positive byte count`() {
        val outcome = interpretRfcommRead(-1)
        assert(outcome !is BluetoothReadOutcome.DataRead) {
            "A naive `numBytes > 0` check would wrongly treat -1 as data in some languages; " +
                "this must never happen in Kotlin's Int comparison, asserted explicitly here."
        }
    }
}

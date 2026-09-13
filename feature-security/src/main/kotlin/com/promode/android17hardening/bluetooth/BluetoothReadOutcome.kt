package com.promode.android17hardening.bluetooth

/** Result of interpreting one [java.io.InputStream.read] call against a Bluetooth socket stream. */
sealed interface BluetoothReadOutcome {
    /** Bytes were read; the loop should continue. */
    data class DataRead(val numBytes: Int) : BluetoothReadOutcome

    /**
     * read() returned -1: end of stream. For apps targeting Android 17 (API 37), this is now
     * how an RFCOMM-based BluetoothSocket signals that the remote device disconnected or the
     * socket was closed -- previously only LE CoC sockets did this, and RFCOMM sockets instead
     * always threw an IOException. See
     * https://developer.android.com/about/versions/17/behavior-changes-17#bluetooth-socket :
     * "the read() method of the InputStream obtained from an RFCOMM-based BluetoothSocket now
     * returns -1 when the socket is closed or the connection is dropped. [...] Apps that rely
     * solely on catching an IOException to break out of a read loop may be impacted."
     */
    data object EndOfStream : BluetoothReadOutcome

    /**
     * read() returned 0. [java.io.InputStream.read] only documents this when the destination
     * buffer itself has zero length, which this reader never passes; surfaced separately rather
     * than folded into [DataRead] so callers don't mistake it for a real byte having arrived.
     */
    data object ZeroByteRead : BluetoothReadOutcome

    /** A return value below -1, which [InputStream.read] never documents. */
    data class UnexpectedNegativeValue(val numBytes: Int) : BluetoothReadOutcome
}

/**
 * Item 4: correct, non-silent handling of the -1 sentinel.
 *
 * A naive implementation that only loops on `numBytes > 0` or only catches IOException (the
 * pre-Android-17 RFCOMM contract) will either busy-loop or silently drop the disconnect signal.
 * This function is the single place that decides what a raw [java.io.InputStream.read] return
 * value means, kept pure and dependency-free so it is unit-testable without a paired device.
 */
fun interpretRfcommRead(numBytes: Int): BluetoothReadOutcome = when {
    numBytes > 0 -> BluetoothReadOutcome.DataRead(numBytes)
    numBytes == -1 -> BluetoothReadOutcome.EndOfStream
    numBytes == 0 -> BluetoothReadOutcome.ZeroByteRead
    else -> BluetoothReadOutcome.UnexpectedNegativeValue(numBytes)
}

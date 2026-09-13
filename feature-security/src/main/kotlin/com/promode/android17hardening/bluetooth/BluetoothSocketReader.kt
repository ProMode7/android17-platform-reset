package com.promode.android17hardening.bluetooth

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException

private const val TAG = "BluetoothSocketReader"

/** Terminal reason a read loop stopped, surfaced to the UI layer. */
enum class ReadLoopStopReason { END_OF_STREAM, IO_EXCEPTION, UNEXPECTED_VALUE }

/**
 * Item 4: reads an RFCOMM [BluetoothSocket] input stream using the platform-recommended loop
 * shape from https://developer.android.com/develop/connectivity/bluetooth/transfer-data --
 * check the return value of every read() call via [interpretRfcommRead] instead of relying
 * solely on catching [IOException] to detect disconnection, which stopped being sufficient for
 * apps targeting Android 17 (see [BluetoothReadOutcome.EndOfStream] for the source citation).
 *
 * [onBytesRead] is invoked with a defensive copy of the filled prefix of the buffer for every
 * [BluetoothReadOutcome.DataRead]. Returns the reason the loop terminated.
 */
fun readRfcommLoop(
    socket: BluetoothSocket,
    bufferSize: Int = 1024,
    onBytesRead: (ByteArray) -> Unit = {}
): ReadLoopStopReason {
    val inputStream = socket.inputStream
    val buffer = ByteArray(bufferSize)

    while (true) {
        val numBytes = try {
            inputStream.read(buffer)
        } catch (e: IOException) {
            Log.d(TAG, "Input stream was disconnected", e)
            return ReadLoopStopReason.IO_EXCEPTION
        }

        when (val outcome = interpretRfcommRead(numBytes)) {
            is BluetoothReadOutcome.DataRead -> {
                onBytesRead(buffer.copyOf(outcome.numBytes))
            }

            BluetoothReadOutcome.EndOfStream -> {
                Log.i(TAG, "read() returned -1: input stream disconnected")
                return ReadLoopStopReason.END_OF_STREAM
            }

            BluetoothReadOutcome.ZeroByteRead -> {
                // No documented meaning for a non-empty buffer; treat as a no-op tick rather
                // than silently spinning forever or misreporting it as data.
                Log.w(TAG, "read() returned 0 for a non-empty buffer")
            }

            is BluetoothReadOutcome.UnexpectedNegativeValue -> {
                Log.e(TAG, "read() returned undocumented value ${outcome.numBytes}")
                return ReadLoopStopReason.UNEXPECTED_VALUE
            }
        }
    }
}

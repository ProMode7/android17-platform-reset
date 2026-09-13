package dev.pramodpatel.handoff.handoff

import android.content.Intent
import dev.pramodpatel.handoff.data.SessionState
import dev.pramodpatel.handoff.data.SessionStateCodec

/**
 * Adapters between [SessionState] and [android.content.Intent] extras, matching
 * the pattern shown in Google's own receiving-side sample code (reading state
 * back out with `intent.getStringExtra("data_id")`, etc., in `onCreate`).
 *
 * This is also the shape used by this sample's local "simulate incoming
 * handoff" flow (see MainActivity) to drive the same restoration code path a
 * real cross-device handoff would use, without an actual second device.
 */
fun Intent.putSessionState(state: SessionState): Intent = apply {
    SessionStateCodec.encode(state).forEach { (key, value) -> putExtra(key, value) }
}

fun Intent.toSessionStateOrNull(): SessionState? {
    val fields = SessionStateCodec.ALL_KEYS.associateWith { getStringExtra(it) }
    return SessionStateCodec.decode(fields)
}

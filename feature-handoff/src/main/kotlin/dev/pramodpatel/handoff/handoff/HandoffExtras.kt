package dev.pramodpatel.handoff.handoff

import android.os.PersistableBundle
import dev.pramodpatel.handoff.data.SessionState
import dev.pramodpatel.handoff.data.SessionStateCodec

/**
 * Adapters between [SessionState] and [android.os.PersistableBundle], the type
 * required by the real, confirmed platform API:
 * `HandoffActivityData.Builder.setExtras(PersistableBundle)`.
 *
 * Verified against the android-37.0 SDK stub jar (`javap android.app.HandoffActivityData$Builder`)
 * and against
 * https://developer.android.com/develop/better-together/continue-on/enable-support.
 */
fun SessionState.toPersistableBundle(): PersistableBundle {
    val bundle = PersistableBundle()
    SessionStateCodec.encode(this).forEach { (key, value) -> bundle.putString(key, value) }
    return bundle
}

fun PersistableBundle.toSessionStateOrNull(): SessionState? {
    val fields = keySet().associateWith { getString(it) }
    return SessionStateCodec.decode(fields)
}

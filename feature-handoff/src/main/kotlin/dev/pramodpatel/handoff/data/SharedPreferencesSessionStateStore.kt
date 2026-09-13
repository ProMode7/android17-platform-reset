package dev.pramodpatel.handoff.data

import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * [SessionStateStore] backed by [SharedPreferences]. Used for this app's
 * on-device "resume where I left off" flow (see [SessionStateStore]'s docs).
 *
 * This class only depends on the [SharedPreferences] interface, not on any
 * concrete Android runtime behavior, so [SessionStateStoreTest] exercises it
 * directly in a JVM unit test against a hand-written fake implementation of
 * that interface rather than needing Robolectric or a device.
 */
class SharedPreferencesSessionStateStore(
    private val prefs: SharedPreferences
) : SessionStateStore {

    override fun save(state: SessionState) {
        prefs.edit {
            SessionStateCodec.encode(state).forEach { (key, value) -> putString(key, value) }
        }
    }

    override fun load(): SessionState? {
        val fields = SessionStateCodec.ALL_KEYS.associateWith { prefs.getString(it, null) }
        return SessionStateCodec.decode(fields)
    }

    override fun clear() {
        prefs.edit { clear() }
    }
}

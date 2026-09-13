package dev.pramodpatel.handoff.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SharedPreferencesSessionStateStoreTest {

    private val prefs = FakeSharedPreferences()
    private val store: SessionStateStore = SharedPreferencesSessionStateStore(prefs)

    @Test
    fun `load returns null when nothing has been saved`() {
        assertNull(store.load())
    }

    @Test
    fun `save then load round-trips the session state`() {
        val state = SessionState("doc-1", "Doc One", 640, 1_000L)
        store.save(state)
        assertEquals(state, store.load())
    }

    @Test
    fun `save overwrites a previous session`() {
        store.save(SessionState("doc-1", "Doc One", 10, 1L))
        store.save(SessionState("doc-2", "Doc Two", 20, 2L))
        assertEquals(SessionState("doc-2", "Doc Two", 20, 2L), store.load())
    }

    @Test
    fun `clear removes the saved session`() {
        store.save(SessionState("doc-1", "Doc One", 10, 1L))
        store.clear()
        assertNull(store.load())
    }

    @Test
    fun `malformed data already in preferences is ignored on load`() {
        // Simulates a corrupt or partially-written preferences file (for
        // example, an interrupted write) rather than going through save().
        val editor = prefs.edit()
        editor.putString(SessionStateCodec.KEY_DOCUMENT_ID, "")
        editor.putString(SessionStateCodec.KEY_SCROLL_POSITION, "not-a-number")
        editor.apply()

        assertNull(store.load())
    }
}

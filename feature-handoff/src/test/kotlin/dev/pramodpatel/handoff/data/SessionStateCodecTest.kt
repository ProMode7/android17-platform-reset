package dev.pramodpatel.handoff.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class SessionStateCodecTest {

    private val sample = SessionState(
        documentId = "handoff-overview",
        documentTitle = "What Handoff Actually Does",
        scrollPositionPx = 842,
        savedAtEpochMillis = 1_732_000_000_000L
    )

    @Test
    fun `encode then decode round-trips exactly`() {
        val encoded = SessionStateCodec.encode(sample)
        val decoded = SessionStateCodec.decode(encoded)
        assertEquals(sample, decoded)
    }

    @Test
    fun `encode produces the documented handoff extras keys`() {
        val encoded = SessionStateCodec.encode(sample)
        assertEquals("handoff-overview", encoded[SessionStateCodec.KEY_DOCUMENT_ID])
        assertEquals("842", encoded[SessionStateCodec.KEY_SCROLL_POSITION])
    }

    @Test
    fun `decode returns null when documentId missing`() {
        val fields = mapOf(
            SessionStateCodec.KEY_SCROLL_POSITION to "100",
            SessionStateCodec.KEY_SAVED_AT to "0"
        )
        assertNull(SessionStateCodec.decode(fields))
    }

    @Test
    fun `decode returns null when documentId blank`() {
        val fields = mapOf(
            SessionStateCodec.KEY_DOCUMENT_ID to "   ",
            SessionStateCodec.KEY_SCROLL_POSITION to "100"
        )
        assertNull(SessionStateCodec.decode(fields))
    }

    @Test
    fun `decode returns null when scroll position missing`() {
        val fields = mapOf(SessionStateCodec.KEY_DOCUMENT_ID to "doc-1")
        assertNull(SessionStateCodec.decode(fields))
    }

    @Test
    fun `decode returns null when scroll position is not a number`() {
        val fields = mapOf(
            SessionStateCodec.KEY_DOCUMENT_ID to "doc-1",
            SessionStateCodec.KEY_SCROLL_POSITION to "not-a-number"
        )
        assertNull(SessionStateCodec.decode(fields))
    }

    @Test
    fun `decode returns null when scroll position is negative`() {
        val fields = mapOf(
            SessionStateCodec.KEY_DOCUMENT_ID to "doc-1",
            SessionStateCodec.KEY_SCROLL_POSITION to "-5"
        )
        assertNull(SessionStateCodec.decode(fields))
    }

    @Test
    fun `decode falls back to documentId when title missing`() {
        val fields = mapOf(
            SessionStateCodec.KEY_DOCUMENT_ID to "doc-1",
            SessionStateCodec.KEY_SCROLL_POSITION to "0"
        )
        val decoded = SessionStateCodec.decode(fields)
        assertEquals("doc-1", decoded?.documentTitle)
    }

    @Test
    fun `decode defaults missing timestamp to zero`() {
        val fields = mapOf(
            SessionStateCodec.KEY_DOCUMENT_ID to "doc-1",
            SessionStateCodec.KEY_SCROLL_POSITION to "0"
        )
        assertEquals(0L, SessionStateCodec.decode(fields)?.savedAtEpochMillis)
    }

    @Test
    fun `decode ignores unrelated extra fields`() {
        val fields = mapOf(
            SessionStateCodec.KEY_DOCUMENT_ID to "doc-1",
            SessionStateCodec.KEY_SCROLL_POSITION to "10",
            "some_unrelated_intent_extra" to "ignored"
        )
        assertEquals("doc-1", SessionStateCodec.decode(fields)?.documentId)
    }

    @Test
    fun `encode rejects payloads over the documented 50KB extras limit`() {
        val oversized = sample.copy(documentTitle = "x".repeat(60_000))
        assertThrows(IllegalArgumentException::class.java) {
            SessionStateCodec.encode(oversized)
        }
    }
}

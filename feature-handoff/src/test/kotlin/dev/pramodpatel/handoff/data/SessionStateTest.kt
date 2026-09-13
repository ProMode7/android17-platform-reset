package dev.pramodpatel.handoff.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SessionStateTest {

    @Test
    fun `constructs with valid fields`() {
        val state = SessionState(
            documentId = "doc-1",
            documentTitle = "Doc One",
            scrollPositionPx = 120,
            savedAtEpochMillis = 1_000L
        )
        assertEquals("doc-1", state.documentId)
        assertEquals(120, state.scrollPositionPx)
    }

    @Test
    fun `rejects blank documentId`() {
        assertThrows(IllegalArgumentException::class.java) {
            SessionState(
                documentId = "   ",
                documentTitle = "Doc",
                scrollPositionPx = 0,
                savedAtEpochMillis = 0L
            )
        }
    }

    @Test
    fun `rejects negative scroll position`() {
        assertThrows(IllegalArgumentException::class.java) {
            SessionState(
                documentId = "doc-1",
                documentTitle = "Doc",
                scrollPositionPx = -1,
                savedAtEpochMillis = 0L
            )
        }
    }

    @Test
    fun `allows zero scroll position`() {
        val state = SessionState("doc-1", "Doc", 0, 0L)
        assertEquals(0, state.scrollPositionPx)
    }
}

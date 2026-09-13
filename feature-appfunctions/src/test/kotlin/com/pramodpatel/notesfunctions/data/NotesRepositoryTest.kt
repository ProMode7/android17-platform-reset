package com.pramodpatel.notesfunctions.data

import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [NotesRepository], the plain-Kotlin logic behind the
 * `createNote` / `listNotes` / `deleteNote` AppFunctions. These run on the
 * local JVM and don't touch AppFunctions, AppSearch, or any Android API.
 */
class NotesRepositoryTest {

    @After
    fun tearDown() {
        NotesRepository.clearForTesting()
    }

    @Test
    fun createNote_addsNoteWithGeneratedIdAndTrimmedTitle() = runTest {
        val note = NotesRepository.createNote(" Buy milk ", "From the grocery store")

        assertTrue(note.id.isNotBlank())
        assertEquals("Buy milk", note.title)
        assertEquals("From the grocery store", note.content)
    }

    @Test
    fun createNote_rejectsBlankTitle() = runTest {
        try {
            NotesRepository.createNote("   ", "content")
            throw AssertionError("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // expected
        }
    }

    @Test
    fun listNotes_returnsNewestFirst() = runTest {
        val first = NotesRepository.createNote("First", "")
        val second = NotesRepository.createNote("Second", "")

        val notes = NotesRepository.listNotes()

        assertEquals(listOf(second, first), notes)
    }

    @Test
    fun deleteNote_removesMatchingNoteAndReturnsTrue() = runTest {
        val note = NotesRepository.createNote("Temp", "")

        val removed = NotesRepository.deleteNote(note.id)

        assertTrue(removed)
        assertTrue(NotesRepository.listNotes().isEmpty())
    }

    @Test
    fun deleteNote_returnsFalseWhenIdIsUnknown() = runTest {
        val removed = NotesRepository.deleteNote("does-not-exist")

        assertFalse(removed)
    }
}

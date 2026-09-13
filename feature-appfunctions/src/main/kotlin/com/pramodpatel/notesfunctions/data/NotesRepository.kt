package com.pramodpatel.notesfunctions.data

import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * In-memory store for [Note]s.
 *
 * This is the single piece of business logic behind the app: both the Compose UI
 * (via [notes]) and the AppFunctions entry points in
 * [com.pramodpatel.notesfunctions.appfunctions.BaseNotesAppFunctionService] read
 * and write through this same repository, so a note created by an agent shows up
 * in the UI and vice versa. It is process-scoped and not persisted to disk; a
 * production app would back this with Room or DataStore instead.
 */
object NotesRepository {

    private val mutex = Mutex()
    private val _notes = MutableStateFlow<List<Note>>(emptyList())

    /** Current notes, newest first. */
    val notes: StateFlow<List<Note>> = _notes

    /**
     * Creates a new note and returns it.
     *
     * @throws IllegalArgumentException if [title] is blank.
     */
    suspend fun createNote(title: String, content: String): Note {
        require(title.isNotBlank()) { "Title must not be blank" }
        val note = Note(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            content = content,
            createdAtEpochMillis = System.currentTimeMillis(),
        )
        mutex.withLock {
            _notes.update { current -> listOf(note) + current }
        }
        return note
    }

    /** Returns all notes currently stored, newest first. */
    suspend fun listNotes(): List<Note> = mutex.withLock { _notes.value }

    /**
     * Deletes the note with the given [noteId].
     *
     * @return `true` if a note was removed, `false` if no note had that id.
     */
    suspend fun deleteNote(noteId: String): Boolean = mutex.withLock {
        val before = _notes.value
        val after = before.filterNot { it.id == noteId }
        _notes.value = after
        after.size != before.size
    }

    /** Test-only helper to reset state between unit tests. */
    internal fun clearForTesting() {
        _notes.value = emptyList()
    }
}

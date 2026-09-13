package com.pramodpatel.notesfunctions.appfunctions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunction
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import com.pramodpatel.notesfunctions.data.CreateNoteParams
import com.pramodpatel.notesfunctions.data.Note
import com.pramodpatel.notesfunctions.data.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Exposes this app's note-taking capabilities as Android 17 AppFunctions.
 *
 * Every `suspend fun` in this class annotated with [AppFunction] is a separate
 * tool an agent (Gemini, or any other holder of `EXECUTE_APP_FUNCTIONS`) can
 * discover and invoke directly, without opening the app. [AppFunctionServiceEntryPoint]
 * tells the `androidx.appfunctions-compiler` KSP processor to generate a concrete
 * `NotesAppFunctionService`, registered in AndroidManifest.xml, that dispatches
 * platform requests into the functions below.
 *
 * AppFunctions are invoked on the main thread by the platform, so each function
 * here hops onto [Dispatchers.IO] before touching [NotesRepository].
 */
@RequiresApi(Build.VERSION_CODES.CINNAMON_BUN) // Android 17 (API 37): android.app.appfunctions
@AppFunctionServiceEntryPoint(
    serviceName = "NotesAppFunctionService",
    appFunctionXmlFileName = "notes_app_function_service",
)
abstract class BaseNotesAppFunctionService : AppFunctionService() {

    /**
     * Creates a new note from [createNoteParams] and returns it.
     *
     * @param createNoteParams The title and content of the note to create.
     * @return The note that was created, including its generated id.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun createNote(createNoteParams: CreateNoteParams): Note =
        withContext(Dispatchers.IO) {
            if (createNoteParams.title.isBlank()) {
                throw AppFunctionInvalidArgumentException("Title must not be blank")
            }
            NotesRepository.createNote(createNoteParams.title, createNoteParams.content)
        }

    /**
     * Lists every note currently stored by this app, newest first.
     *
     * @return All notes currently stored.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun listNotes(): List<Note> = withContext(Dispatchers.IO) {
        NotesRepository.listNotes()
    }

    /**
     * Deletes the note identified by [noteId].
     *
     * @param noteId Id of the note to delete, as returned by `createNote` or `listNotes`.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun deleteNote(noteId: String) {
        withContext(Dispatchers.IO) {
            val removed = NotesRepository.deleteNote(noteId)
            if (!removed) {
                throw AppFunctionElementNotFoundException("No note found with id=$noteId")
            }
        }
    }
}

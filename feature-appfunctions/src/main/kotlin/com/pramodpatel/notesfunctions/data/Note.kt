package com.pramodpatel.notesfunctions.data

import androidx.appfunctions.AppFunctionSerializable

/**
 * A single note.
 *
 * This type is also used as the return type of the `createNote` and `listNotes`
 * AppFunctions, so its KDoc and property KDoc are compiled by the AppFunctions
 * KSP processor into the schema an agent sees when it inspects those functions.
 *
 * @property id Stable identifier for the note, assigned when it is created.
 * @property title Short title of the note.
 * @property content Body text of the note.
 * @property createdAtEpochMillis Time the note was created, in epoch milliseconds.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class Note(
    val id: String,
    val title: String,
    val content: String,
    val createdAtEpochMillis: Long,
)

/**
 * Parameters for creating a new note.
 *
 * @property title Short title of the note. Must not be blank.
 * @property content Body text of the note. May be empty.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class CreateNoteParams(
    val title: String,
    val content: String,
)

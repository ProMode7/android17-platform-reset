package com.pramodpatel.notesfunctions.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pramodpatel.notesfunctions.data.Note
import com.pramodpatel.notesfunctions.data.NotesRepository
import com.pramodpatel.platformreset.design.CodeSnippetBlock
import com.pramodpatel.platformreset.design.DetailBody
import com.pramodpatel.platformreset.design.DetailBreadcrumb
import com.pramodpatel.platformreset.design.DetailHeadline
import com.pramodpatel.platformreset.design.VerifiedRow
import com.pramodpatel.platformreset.design.VerifiedSectionLabel
import kotlinx.coroutines.launch

/**
 * The AppFunctions feature screen: the same [NotesRepository] this app's UI reads and writes is
 * the one [com.pramodpatel.notesfunctions.appfunctions.BaseNotesAppFunctionService] exposes to
 * agents via `@AppFunction`, so a note added here is visible to `listNotes`, and vice versa
 * (verify with `adb shell cmd app_function`, see the root README).
 *
 * Ported from this feature's original standalone MainActivity/NotesApp composable; the note list
 * is rendered as a plain `Column` rather than the original `LazyColumn` so this whole screen,
 * chrome included, is a single scrollable region instead of nesting one scrollable inside another.
 */
@Composable
fun NotesScreen(onBack: (() -> Unit)? = null) {
    val notes by NotesRepository.notes.collectAsState()
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        if (onBack != null) {
            DetailBreadcrumb(breadcrumb = "Android 17 / AppFunctions", onBack = onBack)
        }
        DetailHeadline(
            title = "AppFunctions",
            versionLine = "androidx.appfunctions:1.0.0-alpha11 · API 37+",
        )
        DetailBody(
            "Expose app capabilities as callable tools that Gemini and other on-device agents " +
                "can discover and invoke directly — no manual app-open required. Verified by " +
                "decompiling the real alpha11 AAR: @AppFunction, @AppFunctionSerializable, and " +
                "AppFunctionService are genuine, shipped classes.",
        )
        CodeSnippetBlock(
            code = "@AppFunction\nsuspend fun createNote(\n    params: CreateNoteParams\n): CreateNoteResult",
        )
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            VerifiedSectionLabel()
            VerifiedRow("Builds against the real android-37.0 SDK")
            VerifiedRow("cmd app_function round-trip confirmed on a real device")
            VerifiedRow("Instrumented tests pass on-device; Robolectric leak fixed")
        }

        VerifiedSectionLabel(text = "Live demo")
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = {
                val currentTitle = title
                val currentContent = content
                if (currentTitle.isNotBlank()) {
                    scope.launch { NotesRepository.createNote(currentTitle, currentContent) }
                    title = ""
                    content = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Add note")
        }
        Text(
            text = "Same functions Gemini can call: createNote, listNotes, deleteNote.",
            style = MaterialTheme.typography.bodySmall,
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            notes.forEach { note ->
                NoteRow(note = note, onDelete = { scope.launch { NotesRepository.deleteNote(note.id) } })
            }
        }
    }
}

@Composable
private fun NoteRow(note: Note, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = note.title, style = MaterialTheme.typography.titleMedium)
            if (note.content.isNotBlank()) {
                Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
            }
            TextButton(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}

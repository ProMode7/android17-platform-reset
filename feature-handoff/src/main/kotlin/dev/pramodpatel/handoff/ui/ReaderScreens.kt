package dev.pramodpatel.handoff.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pramodpatel.handoff.data.ReaderDocument
import dev.pramodpatel.handoff.data.RestoreSource
import dev.pramodpatel.handoff.data.SampleDocuments
import dev.pramodpatel.handoff.data.SessionState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/**
 * Root screen. Owns the visible reader UI, reports scroll changes upward via
 * [onSessionStateChanged] (which MainActivity both persists locally and keeps
 * fresh for the real onHandoffActivityDataRequested callback), and exposes a
 * clearly-labeled local simulation of an incoming handoff via
 * [onSimulateIncomingHandoff].
 */
@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@Composable
fun HandoffReaderApp(
    initialState: SessionState,
    restoreSource: RestoreSource,
    handoffEnabled: Boolean,
    onSessionStateChanged: (SessionState) -> Unit,
    onSimulateIncomingHandoff: (SessionState) -> Unit
) {
    var document by remember {
        mutableStateOf(SampleDocuments.byId(initialState.documentId) ?: SampleDocuments.all.first())
    }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var initialRestoreApplied by remember { mutableStateOf(false) }

    fun openDocument(target: ReaderDocument) {
        document = target
        coroutineScope.launch { scrollState.scrollTo(0) }
    }

    // Runs once: applies the position this screen was opened with, whether that
    // came from a real on-device save or a simulated incoming handoff.
    LaunchedEffect(Unit) {
        scrollState.scrollTo(initialState.scrollPositionPx)
        initialRestoreApplied = true
    }

    // Reports the debounced live scroll position upward once the initial
    // restore above has applied, and again whenever the open document changes.
    LaunchedEffect(document, initialRestoreApplied) {
        if (!initialRestoreApplied) return@LaunchedEffect
        snapshotFlow { scrollState.value }
            .debounce(400)
            .collectLatest { px ->
                onSessionStateChanged(
                    SessionState(
                        documentId = document.id,
                        documentTitle = document.title,
                        scrollPositionPx = px,
                        savedAtEpochMillis = System.currentTimeMillis()
                    )
                )
            }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Handoff Reader") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            DocumentTabs(
                documents = SampleDocuments.all,
                selectedId = document.id,
                onSelect = ::openDocument
            )

            if (restoreSource != RestoreSource.NONE) {
                RestoreBanner(restoreSource, initialState)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Text(document.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))
                document.paragraphs.forEach { paragraph ->
                    Text(paragraph, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(16.dp))
                }
            }

            HandoffPanel(
                handoffEnabled = handoffEnabled,
                document = document,
                scrollPx = scrollState.value,
                onSimulateIncomingHandoff = onSimulateIncomingHandoff
            )
        }
    }
}

@Composable
private fun DocumentTabs(
    documents: List<ReaderDocument>,
    selectedId: String,
    onSelect: (ReaderDocument) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        documents.forEach { doc ->
            FilterChip(
                selected = doc.id == selectedId,
                onClick = { onSelect(doc) },
                label = { Text(doc.title, maxLines = 1) }
            )
        }
    }
}

@Composable
private fun RestoreBanner(restoreSource: RestoreSource, state: SessionState) {
    val message = when (restoreSource) {
        RestoreSource.LOCAL_STORE ->
            "Resumed on this device: continuing \"${state.documentTitle}\" at ${state.scrollPositionPx}px from your last session."
        RestoreSource.SIMULATED_HANDOFF ->
            "Restored from a simulated handoff: \"${state.documentTitle}\" at ${state.scrollPositionPx}px, as if it arrived from another device."
        RestoreSource.NONE -> return
    }
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Text(
            message,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun HandoffPanel(
    handoffEnabled: Boolean,
    document: ReaderDocument,
    scrollPx: Int,
    onSimulateIncomingHandoff: (SessionState) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Handoff enabled: ${if (handoffEnabled) "yes" else "no"}",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Current extras: data_id=${document.id}, scroll_position=${scrollPx}px",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = {
                val target = SampleDocuments.all.first { it.id != document.id }
                onSimulateIncomingHandoff(
                    SessionState(
                        documentId = target.id,
                        documentTitle = target.title,
                        scrollPositionPx = 480,
                        savedAtEpochMillis = System.currentTimeMillis()
                    )
                )
            }) {
                Text("Simulate incoming handoff")
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "Restarts this screen with Intent extras shaped like a real Continue On " +
                    "handoff. Exercises the receiving-side restore code for real; does not " +
                    "exercise the OS transport between two physical devices.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

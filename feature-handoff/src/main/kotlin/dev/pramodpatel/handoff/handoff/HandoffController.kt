package dev.pramodpatel.handoff.handoff

import android.app.HandoffActivityData
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import dev.pramodpatel.handoff.data.RestoreSource
import dev.pramodpatel.handoff.data.SampleDocuments
import dev.pramodpatel.handoff.data.SessionState
import dev.pramodpatel.handoff.data.SessionStateStore
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * The logic that used to live directly in this feature's standalone `MainActivity` (see the
 * `android17-handoff-api` source repo), moved here so it is shared, testable Kotlin rather than
 * something re-derived per Activity. `MainActivity` in `:app` is still what actually calls the
 * real platform surface (`Activity.setHandoffEnabled`, `Activity.onHandoffActivityDataRequested`)
 * -- those are instance methods on `Activity` and cannot move into a library module -- but the
 * data it hands to/reads from that surface goes through here.
 */
object HandoffSessionHolder {
    /**
     * Single source of truth for the reading session, shared between the Compose UI and
     * whichever Activity is currently hosting it. Never null: [onHandoffActivityDataRequested]
     * must never return null, so there is always a session, defaulting to the first sample
     * document.
     */
    val state = MutableStateFlow(defaultSessionState())
    val restoreSource = MutableStateFlow(RestoreSource.NONE)
}

fun defaultSessionState(): SessionState {
    val doc = SampleDocuments.all.first()
    return SessionState(
        documentId = doc.id,
        documentTitle = doc.title,
        scrollPositionPx = 0,
        savedAtEpochMillis = System.currentTimeMillis(),
    )
}

/**
 * Resolves what session to show when the Handoff screen (re)appears: an incoming simulated
 * handoff [Intent] takes priority over what was last saved locally, which takes priority over the
 * default. Mirrors the original standalone repo's `MainActivity.onCreate` restore logic exactly.
 */
fun resolveInitialHandoffState(
    intent: Intent?,
    store: SessionStateStore,
): Pair<SessionState, RestoreSource> {
    val incomingHandoff = intent?.toSessionStateOrNull()
    val locallySaved = store.load()
    val restoreSource = when {
        incomingHandoff != null -> RestoreSource.SIMULATED_HANDOFF
        locallySaved != null -> RestoreSource.LOCAL_STORE
        else -> RestoreSource.NONE
    }
    val restored = incomingHandoff ?: locallySaved ?: defaultSessionState()
    return restored to restoreSource
}

/**
 * Builds the real [HandoffActivityData] the platform asks for via
 * `Activity#onHandoffActivityDataRequested`. Identical to the original repo's inline logic in
 * `MainActivity`, just extracted so it is callable from `:app`'s shared MainActivity without
 * duplicating it.
 */
fun buildHandoffActivityData(component: ComponentName, state: SessionState): HandoffActivityData {
    val fallbackUri = Uri.parse(
        "https://example.com/handoff-reader/${state.documentId}?pos=${state.scrollPositionPx}",
    )
    return HandoffActivityData.Builder(component)
        .setExtras(state.toPersistableBundle())
        .setFallbackUri(fallbackUri)
        .build()
}

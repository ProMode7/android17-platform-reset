package dev.pramodpatel.handoff.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pramodpatel.platformreset.design.CodeSnippetBlock
import com.pramodpatel.platformreset.design.DetailBody
import com.pramodpatel.platformreset.design.DetailBreadcrumb
import com.pramodpatel.platformreset.design.DetailHeadline
import com.pramodpatel.platformreset.design.VerifiedRow
import com.pramodpatel.platformreset.design.VerifiedSectionLabel
import dev.pramodpatel.handoff.data.SharedPreferencesSessionStateStore
import dev.pramodpatel.handoff.handoff.HandoffSessionHolder
import dev.pramodpatel.handoff.handoff.putSessionState
import dev.pramodpatel.handoff.handoff.resolveInitialHandoffState

private const val PREFS_NAME = "handoff_reader_session"

/**
 * The Handoff feature screen. Wraps the real, unmodified [HandoffReaderApp] (ported verbatim from
 * the standalone `android17-handoff-api` repo) with this app's shared detail chrome.
 *
 * The single-Activity host (`:app`'s MainActivity) owns the actual `setHandoffEnabled` /
 * `onHandoffActivityDataRequested` platform calls, since those are `Activity` instance methods
 * that cannot live in a library module. This screen reads and writes
 * [HandoffSessionHolder.state], the shared source of truth that callback reads from, and
 * round-trips the real "simulate incoming handoff" flow through an actual
 * `Intent`/`putSessionState`/`toSessionStateOrNull` extras cycle by restarting the same Activity
 * with [android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP] -- MainActivity's `onNewIntent` re-runs
 * [resolveInitialHandoffState] exactly as `onCreate` does.
 */
@Composable
fun HandoffScreen(onBack: (() -> Unit)? = null) {
    val context = LocalContext.current
    val activity = context as? Activity
    val store = remember {
        SharedPreferencesSessionStateStore(context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE))
    }

    LaunchedEffect(Unit) {
        val (restored, source) = resolveInitialHandoffState(activity?.intent, store)
        HandoffSessionHolder.state.value = restored
        HandoffSessionHolder.restoreSource.value = source
    }

    val initialState by HandoffSessionHolder.state.collectAsState()
    val restoreSource by HandoffSessionHolder.restoreSource.collectAsState()
    val handoffEnabled = remember { activity?.isHandoffEnabled() ?: false }

    Column(modifier = Modifier.fillMaxSize()) {
        // Bounded and independently scrollable: this chrome (breadcrumb, headline, body, code
        // snippet, verified checklist) can be longer than a phone screen has room for once the
        // real interactive reader below it also needs space, so it gets its own scroll region
        // instead of pushing the live demo off-screen.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 260.dp)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            if (onBack != null) {
                DetailBreadcrumb(breadcrumb = "Android 17 / Handoff API", onBack = onBack)
            }
            DetailHeadline(
                title = "Handoff API",
                versionLine = "android.app.HandoffActivityData · platform API, API 37+",
            )
            DetailBody(
                "Activity.setHandoffEnabled and onHandoffActivityDataRequested are real, " +
                    "decompiled-and-confirmed android.app.Activity methods -- not the fictitious " +
                    "HandoffClient.updateHandoffState() an early source claimed. This reader hands " +
                    "off which document is open and how far scrolled.",
            )
            CodeSnippetBlock(
                code = "setHandoffEnabled(\n    true,\n    HandoffActivityParams.Builder()\n" +
                    "        .setAllowHandoffWithoutPackageInstalled(true)\n        .build()\n)",
            )
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                VerifiedSectionLabel()
                VerifiedRow("setHandoffEnabled / onHandoffActivityDataRequested confirmed via javap against android-37.0's android.jar")
                VerifiedRow("HandoffActivityData.Builder(ComponentName) is the real constructor this SDK ships -- not the Activity overload shown in Google's docs")
                VerifiedRow("\"Simulate incoming handoff\" exercises the real Intent extras round trip live")
            }
            VerifiedSectionLabel(text = "Live demo")
        }

        Box(modifier = Modifier.weight(1f)) {
            HandoffReaderApp(
                initialState = initialState,
                restoreSource = restoreSource,
                handoffEnabled = handoffEnabled,
                onSessionStateChanged = { updated ->
                    HandoffSessionHolder.state.value = updated
                    store.save(updated)
                },
                onSimulateIncomingHandoff = { state ->
                    val a = activity
                    if (a != null) {
                        val simulated = Intent(a, a.javaClass)
                            .putSessionState(state)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        a.startActivity(simulated)
                    } else {
                        // No hosting Activity (e.g. a preview) -- update local state directly so
                        // the demo still visibly reacts.
                        HandoffSessionHolder.state.value = state
                        HandoffSessionHolder.restoreSource.value =
                            dev.pramodpatel.handoff.data.RestoreSource.SIMULATED_HANDOFF
                    }
                },
            )
        }
    }
}

package com.pramodpatel.platformreset

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.app.HandoffActivityData
import android.app.HandoffActivityDataRequestInfo
import android.app.HandoffActivityParams
import com.pramodpatel.platformreset.design.PlatformResetTheme
import dev.pramodpatel.handoff.data.RestoreSource
import dev.pramodpatel.handoff.handoff.HandoffSessionHolder
import dev.pramodpatel.handoff.handoff.buildHandoffActivityData
import dev.pramodpatel.handoff.handoff.toSessionStateOrNull

/**
 * The single Activity hosting every feature. Navigation Compose (phone) or the two-pane dashboard
 * (large screens, see [PlatformResetApp]) decide what's on screen; this class only holds the
 * pieces that must live on an actual `Activity` instance:
 *
 * - The real Handoff API surface (`setHandoffEnabled`, `onHandoffActivityDataRequested`) --
 *   `android.app.Activity` instance methods that cannot move into `:feature-handoff`, a library
 *   module. The data those calls read from and write to lives in
 *   [dev.pramodpatel.handoff.handoff.HandoffSessionHolder], shared with the Compose screen.
 * - `onNewIntent`, which re-runs the same restore path `onCreate` does, for the Handoff feature's
 *   "simulate incoming handoff" flow: that flow restarts this exact Activity
 *   (`FLAG_ACTIVITY_SINGLE_TOP`) with Intent extras shaped like a real Continue On handoff.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        applyIncomingIntent(intent, navigateOnRestore = false)

        // Real, confirmed platform API (android.app.Activity#setHandoffEnabled), verified by
        // decompiling android-37.0's android.jar -- see feature-handoff's HandoffScreen kdoc and
        // the root README. Enabled unconditionally: this Activity always has a current reading
        // session in HandoffSessionHolder (defaulting to the first sample document) worth handing
        // off, whichever feature screen happens to be in front.
        setHandoffEnabled(
            true,
            HandoffActivityParams.Builder()
                .setAllowHandoffWithoutPackageInstalled(true)
                .build(),
        )

        setContent {
            PlatformResetTheme {
                PlatformResetApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        applyIncomingIntent(intent, navigateOnRestore = true)
    }

    /**
     * Real platform callback: `android.app.Activity#onHandoffActivityDataRequested`. Google's
     * docs say the system invokes this both when the activity backgrounds (save state for a
     * possible later handoff) and when the user actively triggers a handoff in the foreground
     * (`HandoffActivityDataRequestInfo.isActiveRequest()`); it must never return null. The actual
     * `HandoffActivityData` construction is [buildHandoffActivityData], shared with the original
     * standalone repo's logic.
     */
    override fun onHandoffActivityDataRequested(
        handoffRequestInfo: HandoffActivityDataRequestInfo,
    ): HandoffActivityData {
        return buildHandoffActivityData(
            ComponentName(this, MainActivity::class.java),
            HandoffSessionHolder.state.value,
        )
    }

    private fun applyIncomingIntent(intent: Intent?, navigateOnRestore: Boolean) {
        val incoming = intent?.toSessionStateOrNull() ?: return
        HandoffSessionHolder.state.value = incoming
        HandoffSessionHolder.restoreSource.value = RestoreSource.SIMULATED_HANDOFF
        if (navigateOnRestore) {
            NavigationSignal.requestedFeatureId.value = FeatureCatalog.HANDOFF
        }
    }
}

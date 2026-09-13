package com.pramodpatel.platformreset

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Lets [MainActivity]'s `onNewIntent` (a plain Activity callback, not a composable) ask whichever
 * Compose layout is currently active -- the phone [PhoneNavHost] or the tablet [TwoPaneDashboard]
 * -- to bring a given feature to the front. Used for the Handoff feature's "simulate incoming
 * handoff" flow: the restarted Activity's `onNewIntent` decodes the real Intent extras, updates
 * `HandoffSessionHolder`, and sets this so the user actually sees the restored reading session
 * instead of it silently updating off-screen.
 */
object NavigationSignal {
    val requestedFeatureId = MutableStateFlow<String?>(null)
}

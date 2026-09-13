package dev.pramodpatel.handoff.data

/**
 * A snapshot of "where the reader is": which document, how far scrolled (in
 * pixels), and when that position was captured.
 *
 * This is the one piece of app state that gets carried across three different
 * boundaries in this sample:
 *  - out through the platform Handoff API, as extras on [android.app.HandoffActivityData]
 *  - in through a receiving Activity's [android.content.Intent] extras
 *  - persisted locally so the app can resume on this same device after a
 *    process death or restart, independent of any cross-device transport
 *
 * See [dev.pramodpatel.handoff.data.SessionStateCodec] for the shared encoding used
 * across all three, and the README for which of those three is real platform
 * integration versus a local simulation.
 */
data class SessionState(
    val documentId: String,
    val documentTitle: String,
    val scrollPositionPx: Int,
    val savedAtEpochMillis: Long
) {
    init {
        require(documentId.isNotBlank()) { "documentId must not be blank" }
        require(scrollPositionPx >= 0) { "scrollPositionPx must not be negative" }
    }
}

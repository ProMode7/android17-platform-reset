package dev.pramodpatel.handoff.data

/**
 * Converts [SessionState] to and from a flat, string-keyed field map.
 *
 * The flat, all-String shape is deliberate: it has to survive three different
 * platform boundaries with different capabilities, and a lowest-common-denominator
 * shape is the simplest thing that works on all of them:
 *  - `android.os.PersistableBundle`, the type Handoff's own
 *    `HandoffActivityData.Builder.setExtras()` requires for outgoing state
 *    (see [dev.pramodpatel.handoff.handoff.HandoffExtras])
 *  - `android.content.Intent` extras, which is how Google's own documentation
 *    shows the receiving Activity reading the handed-off state back out
 *    (`intent.getStringExtra("data_id")`, etc.)
 *  - `SharedPreferences`, used here for this app's own on-device "resume where
 *    I left off" persistence, which has nothing to do with cross-device Handoff
 *
 * Google's sample code calls `PersistableBundle.putInt()` for the scroll position;
 * this codec stores every field as a String instead, purely to keep encode/decode
 * symmetric and trivially round-trippable across all three destinations above
 * without per-field type switches. That's a design simplification on top of the
 * confirmed API, not a claim about what the platform requires.
 *
 * Kept as pure Kotlin (no Android types) so it can be exercised directly by JVM
 * unit tests without Robolectric or a device/emulator.
 */
object SessionStateCodec {
    const val KEY_DOCUMENT_ID = "data_id"
    const val KEY_DOCUMENT_TITLE = "doc_title"
    const val KEY_SCROLL_POSITION = "scroll_position"
    const val KEY_SAVED_AT = "saved_at_millis"

    val ALL_KEYS = listOf(KEY_DOCUMENT_ID, KEY_DOCUMENT_TITLE, KEY_SCROLL_POSITION, KEY_SAVED_AT)

    /**
     * The Handoff extras ceiling Google's documentation states (50KB). Encoded
     * session state here is a few dozen bytes, nowhere near this limit — the check
     * exists to document the constraint and fail loudly rather than to do
     * meaningful work for this sample's own data.
     */
    const val MAX_ENCODED_BYTES = 50 * 1024

    fun encode(state: SessionState): Map<String, String> {
        val encoded = linkedMapOf(
            KEY_DOCUMENT_ID to state.documentId,
            KEY_DOCUMENT_TITLE to state.documentTitle,
            KEY_SCROLL_POSITION to state.scrollPositionPx.toString(),
            KEY_SAVED_AT to state.savedAtEpochMillis.toString()
        )
        val approxByteSize = encoded.entries.sumOf { it.key.length + it.value.length }
        require(approxByteSize <= MAX_ENCODED_BYTES) {
            "Encoded session state ($approxByteSize bytes) exceeds the documented " +
                "50KB Handoff extras limit"
        }
        return encoded
    }

    /**
     * Returns `null` rather than throwing on missing or malformed input. Both
     * inbound sources of this data — an arbitrary launched [android.content.Intent],
     * or extras restored from a previous, possibly-stale, on-device save — are
     * untrusted input the UI needs to degrade against gracefully rather than crash on.
     */
    fun decode(fields: Map<String, String?>): SessionState? {
        val documentId = fields[KEY_DOCUMENT_ID]?.takeIf { it.isNotBlank() } ?: return null
        val documentTitle = fields[KEY_DOCUMENT_TITLE]?.takeIf { it.isNotBlank() } ?: documentId
        val scrollPosition = fields[KEY_SCROLL_POSITION]?.toIntOrNull() ?: return null
        if (scrollPosition < 0) return null
        val savedAt = fields[KEY_SAVED_AT]?.toLongOrNull() ?: 0L
        return runCatching {
            SessionState(documentId, documentTitle, scrollPosition, savedAt)
        }.getOrNull()
    }
}

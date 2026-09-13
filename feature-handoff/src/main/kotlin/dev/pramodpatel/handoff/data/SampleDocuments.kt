package dev.pramodpatel.handoff.data

/** A single in-app "document" the reader screen can display and scroll through. */
data class ReaderDocument(
    val id: String,
    val title: String,
    val paragraphs: List<String>
)

/**
 * Canned content for the sample. There is nothing dynamic here — this exists so
 * the reader screen has more than one document and enough text per document to
 * make scroll-position handoff a meaningful, visible thing to demonstrate.
 */
object SampleDocuments {
    val all: List<ReaderDocument> = listOf(
        ReaderDocument(
            id = "handoff-overview",
            title = "What Handoff Actually Does",
            paragraphs = listOf(
                "Handoff, shipped as part of Android 17 under the \"Continue On\" feature, lets a user start an activity on one device and pick it back up on another. The system runs a background service that surfaces recently used, handoff-enabled activities from a user's other nearby devices, and shows a one-tap suggestion for them — for example, in a tablet's taskbar when the same user was just reading on their phone.",
                "Support is opt-in and scoped to individual activities, not the whole app. An activity calls setHandoffEnabled(true, params) once it has something worth resuming, and only from that point on can the system ask it for a snapshot of its state.",
                "There are three shapes the handoff can take: an app-to-app deep link that recreates the same activity on the receiving device, an app-to-app link with a web fallback for when the app isn't installed there, and a direct-to-web handoff where a URL is the primary experience from the start.",
                "None of this involves a new client SDK. It is a pair of new methods on android.app.Activity, plus two new value classes, android.app.HandoffActivityData and android.app.HandoffActivityParams. That surface is small enough to read end to end in a few minutes, which this sample's handoff package does."
            )
        ),
        ReaderDocument(
            id = "scroll-state-design",
            title = "Why Scroll Position Is the State Worth Sending",
            paragraphs = listOf(
                "Not every piece of activity state is worth handing off. Handoff's extras are capped at 50KB and round-trip through a system service, so the honest question for any screen is: what is the smallest piece of state that, restored on another device, actually feels like picking up where you left off?",
                "For a reader, that's two fields: which document was open, and how far down it the user had scrolled. Recreating font size, theme, or scroll physics on the receiving device is the receiving activity's job, not the handoff payload's.",
                "This sample's SessionState type is exactly that: a document ID, a scroll offset in pixels, a display title (so the receiving side has something to show immediately, before it has necessarily loaded the document), and a timestamp for surfacing \"last read 2 minutes ago\" style UI.",
                "Keeping the payload this small also means encoding it is boring, and boring is what you want for code that runs on every backgrounding of a foreground activity — see HandoffActivityDataRequestInfo's two trigger conditions in the next section."
            )
        ),
        ReaderDocument(
            id = "two-trigger-conditions",
            title = "The Two Times the System Asks For State",
            paragraphs = listOf(
                "onHandoffActivityDataRequested() is called by the system, not the app, and Google's documentation is specific about when: once when the activity is going into the background and its state should be saved in case a handoff is requested later, and again if the activity is in the foreground and the user has actively triggered a handoff right now.",
                "HandoffActivityDataRequestInfo.isActiveRequest() is how the callback tells those two cases apart. The documentation's guidance for the active-request case is to treat it as a signal to (asynchronously) show \"handoff in progress\" UI — not to do blocking work inside the callback itself.",
                "Because the callback can fire at any point after handoff is enabled, the value it returns has to reflect current state, not state computed lazily when the callback runs. This sample keeps a plain field on the Activity, updated every time the reader's scroll position or open document changes, precisely so the callback has nothing left to compute — it just packages what's already there.",
                "This is also why the callback must never return null: Google's docs call this out explicitly, since there is no default HandoffActivityData a generic activity could construct on the app's behalf."
            )
        )
    )

    fun byId(id: String): ReaderDocument? = all.firstOrNull { it.id == id }
}

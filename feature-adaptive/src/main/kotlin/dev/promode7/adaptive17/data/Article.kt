package dev.promode7.adaptive17.data

/** A single article in the sample list/detail feed. */
data class Article(
    val id: String,
    val title: String,
    val author: String,
    val summary: String,
    val body: String,
)

/** Static sample content; a real app would load this from a repository/network layer. */
object ArticleRepository {

    val articles: List<Article> = listOf(
        Article(
            id = "escape-hatch",
            title = "The resizeableActivity escape hatch is gone",
            author = "Pramod Patel",
            summary = "Android 16 let you opt out of large-screen resizing for one release. " +
                "Android 17 removes that opt-out entirely.",
            body = "android:resizeableActivity=\"false\" used to guarantee your activity would " +
                "never be shown in a resized or multi-window container. Android 16 (API 36) " +
                "started ignoring that attribute on large screens (smallest width >= 600dp) for " +
                "apps targeting API 36, but it shipped a temporary manifest property, " +
                "android.window.PROPERTY_COMPAT_ALLOW_RESTRICTED_RESIZABILITY, that let you keep " +
                "the old behavior for one more cycle. Android 17 (API 37) removes that property's " +
                "effect. Once an app targets API 37, resizeableActivity, screenOrientation, " +
                "minAspectRatio and maxAspectRatio are all ignored on large screens, with no " +
                "opt-out. Ship a UI that adapts, or ship a UI that gets stretched and " +
                "letterboxed for you.",
        ),
        Article(
            id = "window-size-class",
            title = "Window size classes, not device types",
            author = "Pramod Patel",
            summary = "androidx.window's WindowSizeClass buckets the available window space, " +
                "not the physical device, so the same phone can be compact or medium depending " +
                "on how it's split.",
            body = "A WindowSizeClass is computed from the width and height, in dp, of the " +
                "window your activity currently occupies -- not from the physical screen size. " +
                "That is deliberate: the same foldable can report a compact width when folded " +
                "and a medium or expanded width when unfolded, and the same tablet can report a " +
                "compact width when your app is docked to one side of a split screen. Building " +
                "layout logic against the window size class, and recomposing when it changes, is " +
                "what makes an app correct in multi-window and desktop windowing without special " +
                "casing every device.",
        ),
        Article(
            id = "two-pane",
            title = "List-detail is the canonical adaptive pattern",
            author = "Pramod Patel",
            summary = "A single-pane list that pushes to a detail screen on phones, and a " +
                "two-pane list+detail split on anything wider, is the most common adaptive " +
                "layout in the Android and iOS ecosystems.",
            body = "This sample intentionally keeps the pattern simple: one list of articles, " +
                "one detail reader. Below the medium width breakpoint (600dp), selecting an " +
                "article navigates to a full-screen detail view with a back button. At medium " +
                "width and above, the list and the detail view are shown side by side and " +
                "selecting an article just updates the detail pane in place. The layout decision " +
                "is a single, unit-testable function -- see LayoutDecision.kt and " +
                "LayoutDecisionTest.kt -- and everything else is ordinary Compose.",
        ),
        Article(
            id = "google-play-deadline",
            title = "This is not optional forever",
            author = "Pramod Patel",
            summary = "Google Play requires new apps and updates to target the current API " +
                "level on a yearly cadence, which is what eventually forces this migration for " +
                "every app on the Play Store.",
            body = "Google Play's target API level policy requires new apps and updates to " +
                "target a recent API level on a rolling schedule. When that requirement reaches " +
                "API level 37, every app being updated on the Play Store inherits the Android 17 " +
                "large-screen behavior automatically, whether or not the team has audited their " +
                "layouts for it. Auditing now, while API 37 is still optional, is cheaper than " +
                "auditing under a submission deadline.",
        ),
    )
}

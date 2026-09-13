package dev.promode7.adaptive17.ui

import androidx.window.core.layout.WindowSizeClass

/**
 * The two layout modes this sample switches between.
 *
 * [SinglePane] is a phone-width window: the list and the detail reader are separate screens and
 * navigating between them replaces the visible content.
 *
 * [TwoPane] is a medium-width-or-wider window (an unfolded foldable, a tablet, a split-screen or
 * freeform desktop window): the list and the detail reader are shown side by side.
 */
enum class LayoutMode { SinglePane, TwoPane }

/**
 * Pure, platform-independent layout decision: given the window's [WindowSizeClass], decide
 * whether to render one pane or two.
 *
 * This is the function Android 17 makes mandatory in spirit. Before, an app could declare
 * `android:resizeableActivity="false"` and rely on the system to always hand it one fixed-shape
 * window. Android 17 (API 37) removes that guarantee on large screens (see README.md), so the
 * window this function receives can legitimately be anything from a narrow phone window to a
 * resizable desktop window -- the UI has to decide its layout from the space it is actually
 * given, every time that space changes.
 *
 * [WindowSizeClass] itself has no Android framework dependency (it lives in the KMP-friendly
 * androidx.window.core.layout package), which is what makes this function testable with plain
 * JUnit -- see LayoutDecisionTest.kt.
 */
fun pickLayoutMode(windowSizeClass: WindowSizeClass): LayoutMode =
    if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
        LayoutMode.TwoPane
    } else {
        LayoutMode.SinglePane
    }

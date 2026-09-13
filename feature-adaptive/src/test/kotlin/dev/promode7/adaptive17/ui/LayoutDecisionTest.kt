package dev.promode7.adaptive17.ui

import androidx.window.core.layout.WindowSizeClass
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * [pickLayoutMode] is pure Kotlin (no Android framework, no Robolectric, no device needed):
 * [WindowSizeClass] lives in androidx.window.core.layout and is computed from plain width/height
 * dp values, so this runs as an ordinary JVM unit test under ./gradlew testDebugUnitTest.
 */
class LayoutDecisionTest {

    @Test
    fun `phone portrait width stays single pane`() {
        // A typical phone in portrait, e.g. Pixel-class devices, is well under the 600dp
        // medium-width breakpoint.
        val windowSizeClass = WindowSizeClass.compute(dpWidth = 360f, dpHeight = 800f)

        assertEquals(LayoutMode.SinglePane, pickLayoutMode(windowSizeClass))
    }

    @Test
    fun `phone landscape just under the medium breakpoint stays single pane`() {
        val windowSizeClass = WindowSizeClass.compute(dpWidth = 599f, dpHeight = 360f)

        assertEquals(LayoutMode.SinglePane, pickLayoutMode(windowSizeClass))
    }

    @Test
    fun `exactly the medium width breakpoint switches to two pane`() {
        val windowSizeClass = WindowSizeClass.compute(dpWidth = 600f, dpHeight = 400f)

        assertEquals(LayoutMode.TwoPane, pickLayoutMode(windowSizeClass))
    }

    @Test
    fun `unfolded foldable width is two pane`() {
        val windowSizeClass = WindowSizeClass.compute(dpWidth = 700f, dpHeight = 512f)

        assertEquals(LayoutMode.TwoPane, pickLayoutMode(windowSizeClass))
    }

    @Test
    fun `tablet width is two pane`() {
        val windowSizeClass = WindowSizeClass.compute(dpWidth = 840f, dpHeight = 1024f)

        assertEquals(LayoutMode.TwoPane, pickLayoutMode(windowSizeClass))
    }

    @Test
    fun `expanded desktop windowing width is two pane`() {
        val windowSizeClass = WindowSizeClass.compute(dpWidth = 1400f, dpHeight = 900f)

        assertEquals(LayoutMode.TwoPane, pickLayoutMode(windowSizeClass))
    }

    @Test
    fun `a narrow freeform desktop window falls back to single pane`() {
        // Android 17's point is that this shape is now legal even though resizeableActivity used
        // to prevent it: a desktop window the user has resized down to phone-like width.
        val windowSizeClass = WindowSizeClass.compute(dpWidth = 420f, dpHeight = 900f)

        assertEquals(LayoutMode.SinglePane, pickLayoutMode(windowSizeClass))
    }
}

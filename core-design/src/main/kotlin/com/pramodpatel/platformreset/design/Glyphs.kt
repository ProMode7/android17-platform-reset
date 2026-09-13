package com.pramodpatel.platformreset.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Small vector glyphs redrawn from the exact SVG paths in the approved mockups (Main.dc.html,
 * Detail.dc.html, TabletHome.dc.html), rather than substituted with a generic Material icon.
 */

/** The sage-green "verified/working" status dot on each home-dashboard row. Matches the mockup's
 * `<circle r="4.5" fill="#7FA87A"/>` (9x9 viewBox) and the tablet rail's 8x8 variant. */
@Composable
fun StatusDot(modifier: Modifier = Modifier, diameter: Dp = 9.dp) {
    Canvas(modifier = modifier.size(diameter)) {
        drawCircle(color = PlatformResetColors.StatusDotGreen, radius = size.minDimension / 2f)
    }
}

/** The small right-pointing row chevron: `M1 1L6 6L1 11` in a 7x12 viewBox, stroke #6B5B47. */
@Composable
fun RowChevron(modifier: Modifier = Modifier, color: Color = PlatformResetColors.ChevronStroke) {
    Canvas(modifier = modifier.size(width = 7.dp, height = 12.dp)) {
        val sx = size.width / 7f
        val sy = size.height / 12f
        val path = Path().apply {
            moveTo(1f * sx, 1f * sy)
            lineTo(6f * sx, 6f * sy)
            lineTo(1f * sx, 11f * sy)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.6f * sx, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

/** The back-navigation chevron on detail screens: `M7 1L1.5 7L7 13` in an 8x14 viewBox, accent
 * color, thicker stroke than [RowChevron]. */
@Composable
fun BackChevron(modifier: Modifier = Modifier, color: Color = PlatformResetColors.Accent) {
    Canvas(modifier = modifier.size(width = 8.dp, height = 14.dp)) {
        val sx = size.width / 8f
        val sy = size.height / 14f
        val path = Path().apply {
            moveTo(7f * sx, 1f * sy)
            lineTo(1.5f * sx, 7f * sy)
            lineTo(7f * sx, 13f * sy)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.8f * sx, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

/** The checkmark used in a detail screen's "Verified" list: `M2.5 8L5.8 11L12.5 3.5` in a 15x15
 * viewBox, sage green, matching [StatusDot]'s color so the two read as the same signal. */
@Composable
fun CheckMark(modifier: Modifier = Modifier, color: Color = PlatformResetColors.StatusDotGreen) {
    Canvas(modifier = modifier.size(15.dp)) {
        val s = size.width / 15f
        val path = Path().apply {
            moveTo(2.5f * s, 8f * s)
            lineTo(5.8f * s, 11f * s)
            lineTo(12.5f * s, 3.5f * s)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.7f * s, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

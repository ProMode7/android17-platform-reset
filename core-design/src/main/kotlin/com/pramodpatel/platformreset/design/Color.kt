package com.pramodpatel.platformreset.design

import androidx.compose.ui.graphics.Color

/**
 * Exact hex values pulled from the approved design mockups (Main.dc.html, Detail.dc.html,
 * TabletHome.dc.html) -- not approximated. Do not "round" these to a Material tonal palette; the
 * whole point of this design is a flat, warm, dark, non-Material look.
 */
object PlatformResetColors {
    /** Dark background, warm rather than neutral (a hint of brown, not pure black). */
    val Background = Color(0xFF15120D)

    /** Warm off-white body/heading text. */
    val TextPrimary = Color(0xFFF3EDE3)

    /** Amber/cinnamon accent -- links, active states, version/API mono lines. */
    val Accent = Color(0xFFE2954A)

    /** Accent hover/pressed state. */
    val AccentHover = Color(0xFFF0AC6B)

    /** Body copy under a headline (e.g. the home dashboard's subhead). */
    val TextBody = Color(0xFFA89B87)

    /** Body copy inside a detail screen's descriptive paragraph. */
    val TextBodyDetail = Color(0xFFC9BDA9)

    /** Mono subtitle under a list-row title, and section-label mono text. */
    val TextMonoSecondary = Color(0xFF8A7C67)

    /** Very muted mono text -- the "build cinnamon_bun" easter egg, breadcrumb labels. */
    val TextMonoMuted = Color(0xFF5C503F)

    /** Faintest mono text -- footer attribution line. */
    val TextMonoFaint = Color(0xFF4E4433)

    /** Chevron stroke color on list rows and back-nav arrows. */
    val ChevronStroke = Color(0xFF6B5B47)

    /** Hairline under the home dashboard header. */
    val DividerHeader = Color(0xFF2A2318)

    /** Hairline between list rows. */
    val DividerRow = Color(0xFF221C13)

    /** Hairline separating the tablet list rail from the detail pane. */
    val DividerRail = Color(0xFF241E15)

    /** Status dot -- sage green, signals "verified/working" per row. */
    val StatusDotGreen = Color(0xFF7FA87A)

    /** Code-snippet block background. */
    val CodeBackground = Color(0xFF1C170F)

    /** Code-snippet block border. */
    val CodeBorder = Color(0xFF332A1E)

    /** Code-snippet block text. */
    val CodeText = Color(0xFFD8CBB4)

    /** Tablet rail: background of the currently-selected row. */
    val RailSelectedBackground = Color(0xFF1E1912)
}

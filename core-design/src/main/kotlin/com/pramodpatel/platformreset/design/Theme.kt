package com.pramodpatel.platformreset.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The whole app is intentionally always-dark: the approved mockups (Main.dc.html,
 * Detail.dc.html, TabletHome.dc.html) specify one fixed dark, warm palette, not a
 * light/dark pair. [isSystemInDarkTheme] is read only so a future light variant has an
 * obvious seam to plug into; today both branches resolve to the same scheme.
 */
// A muted brick-red, used only for the rare "check failed" state; not part of the mockups
// (which show every check passing) but needed so feature screens have a real failure color
// instead of Material's default red, which would clash with the warm palette.
private val MutedFailureRed = androidx.compose.ui.graphics.Color(0xFFB5624A)

private val PlatformResetColorScheme = darkColorScheme(
    background = PlatformResetColors.Background,
    surface = PlatformResetColors.Background,
    onBackground = PlatformResetColors.TextPrimary,
    onSurface = PlatformResetColors.TextPrimary,
    primary = PlatformResetColors.Accent,
    onPrimary = PlatformResetColors.Background,
    secondary = PlatformResetColors.TextMonoSecondary,
    onSecondary = PlatformResetColors.TextPrimary,
    error = MutedFailureRed,
    onError = PlatformResetColors.TextPrimary,
)

private val PlatformResetTypography = Typography(
    headlineLarge = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 37.sp),
    headlineMedium = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 34.sp),
    headlineSmall = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 21.sp),
    titleSmall = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Normal, fontSize = 14.5.sp, lineHeight = 23.sp),
    bodySmall = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Normal, fontSize = 13.5.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = PlexMono, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.2.sp),
    labelMedium = TextStyle(fontFamily = PlexMono, fontWeight = FontWeight.Normal, fontSize = 11.5.sp, lineHeight = 15.sp),
    labelSmall = TextStyle(fontFamily = PlexMono, fontWeight = FontWeight.Normal, fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 1.3.sp),
)

@Composable
fun PlatformResetTheme(content: @Composable () -> Unit) {
    isSystemInDarkTheme() // seam for a future light theme; see kdoc above
    MaterialTheme(
        colorScheme = PlatformResetColorScheme,
        typography = PlatformResetTypography,
        content = content,
    )
}

/** Semantic aliases so feature screens can reach for a name instead of a raw hex value. */
object PlatformResetTypeStyles {
    val monoLabelUppercase: TextStyle
        @Composable get() = MaterialTheme.typography.labelSmall.copy(color = PlatformResetColors.Accent)

    val monoSubtitle: TextStyle
        @Composable get() = MaterialTheme.typography.labelMedium.copy(color = PlatformResetColors.TextMonoSecondary)
}

package com.pramodpatel.platformreset.design

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * The flat home-dashboard row from Main.dc.html / TabletHome.dc.html: a status dot, a title +
 * mono subtitle stack, and a trailing chevron -- no card, no rounded corners, no shadow,
 * separated from its neighbor by a single hairline. [selected] renders the tablet rail's active
 * row (accent left border, tinted background, accent title color); phone rows never pass it.
 */
@Composable
fun PlatformResetListRow(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    showChevron: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val base = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    Row(
        modifier = base
            .fillMaxWidth()
            .then(
                if (selected) {
                    Modifier
                        .background(PlatformResetColors.RailSelectedBackground)
                        .drawBehind {
                            drawRect(
                                color = PlatformResetColors.Accent,
                                size = size.copy(width = 2.dp.toPx()),
                            )
                        }
                } else {
                    Modifier
                },
            )
            .padding(
                PaddingValues(
                    start = if (selected) 26.dp else 24.dp,
                    end = 24.dp,
                    top = 16.dp,
                    bottom = 16.dp,
                ),
            ),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusDot()
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) PlatformResetColors.Accent else PlatformResetColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = PlatformResetColors.TextMonoSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (showChevron) {
            RowChevron()
        }
    }
}

/** A single hairline row separator, matching the mockup's 1px `#221C13` divider. */
@Composable
fun RowDivider(modifier: Modifier = Modifier) {
    androidx.compose.material3.HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = PlatformResetColors.DividerRow,
    )
}

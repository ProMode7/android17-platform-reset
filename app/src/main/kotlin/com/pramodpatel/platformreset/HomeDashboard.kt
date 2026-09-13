package com.pramodpatel.platformreset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pramodpatel.platformreset.design.PlatformResetColors
import com.pramodpatel.platformreset.design.PlatformResetListRow
import com.pramodpatel.platformreset.design.RowDivider

/**
 * The phone home dashboard, matching Main.dc.html exactly: mono "Android 17 · API 37" eyebrow,
 * "Platform Reset" headline, subhead, the "build cinnamon_bun" easter egg (Android 17's real
 * internal codename -- confirmed by decompiling the actual SDK), a flat hairline-separated list
 * of the five features, and a footer attribution line.
 */
@Composable
fun HomeDashboard(
    selectedFeatureId: String? = null,
    onFeatureClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 56.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "ANDROID 17 · API 37",
                style = MaterialTheme.typography.labelSmall,
                color = PlatformResetColors.Accent,
            )
            Text(
                text = "Platform Reset",
                style = MaterialTheme.typography.headlineLarge,
                color = PlatformResetColors.TextPrimary,
            )
            Text(
                text = "Five changes shipped in the platform layer — verified against the real " +
                    "SDK, not the changelog.",
                style = MaterialTheme.typography.bodyMedium,
                color = PlatformResetColors.TextBody,
            )
            Text(
                text = "build cinnamon_bun",
                style = MaterialTheme.typography.labelMedium,
                color = PlatformResetColors.TextMonoMuted,
            )
        }

        RowDivider(modifier = Modifier.padding(horizontal = 24.dp))

        Column {
            FeatureCatalog.items.forEach { item ->
                PlatformResetListRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    selected = item.id == selectedFeatureId,
                    onClick = { onFeatureClick(item.id) },
                )
                RowDivider()
            }
        }

        Text(
            text = "github.com/ProMode7 · companion to the Medium series",
            style = MaterialTheme.typography.labelMedium,
            color = PlatformResetColors.TextMonoFaint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 28.dp),
        )
    }
}

/**
 * The 420dp list rail used on large screens (TabletHome.dc.html): the same header and rows as
 * [HomeDashboard], but without the footer line's bottom padding driving the whole screen's height
 * (the rail scrolls independently of the detail pane it sits beside).
 */
@Composable
fun FeatureRail(
    selectedFeatureId: String,
    onFeatureClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "ANDROID 17 · API 37",
                style = MaterialTheme.typography.labelSmall,
                color = PlatformResetColors.Accent,
            )
            Text(
                text = "Platform Reset",
                style = MaterialTheme.typography.headlineMedium,
                color = PlatformResetColors.TextPrimary,
            )
            Text(
                text = "build cinnamon_bun",
                style = MaterialTheme.typography.labelMedium,
                color = PlatformResetColors.TextMonoMuted,
            )
        }

        RowDivider(modifier = Modifier.padding(horizontal = 24.dp))

        Column(modifier = Modifier.weight(1f)) {
            FeatureCatalog.items.forEach { item ->
                PlatformResetListRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    selected = item.id == selectedFeatureId,
                    onClick = { onFeatureClick(item.id) },
                )
                RowDivider()
            }
        }

        Text(
            text = "github.com/ProMode7",
            style = MaterialTheme.typography.labelMedium,
            color = PlatformResetColors.TextMonoFaint,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 18.dp, bottom = 24.dp),
        )
    }
}

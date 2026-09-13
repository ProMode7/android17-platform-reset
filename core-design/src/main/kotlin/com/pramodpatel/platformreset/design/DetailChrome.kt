package com.pramodpatel.platformreset.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Back chevron + uppercase mono breadcrumb, matching Detail.dc.html's top row exactly. */
@Composable
fun DetailBreadcrumb(
    breadcrumb: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onBack != null) Modifier.clickable(onClick = onBack) else Modifier)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BackChevron()
        Text(
            text = breadcrumb.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = PlatformResetColors.TextMonoSecondary,
        )
    }
}

/** Large feature headline + mono version/API line in accent color, as in Detail.dc.html. */
@Composable
fun DetailHeadline(
    title: String,
    versionLine: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall, color = PlatformResetColors.TextPrimary)
        Text(text = versionLine, style = MaterialTheme.typography.labelLarge, color = PlatformResetColors.Accent)
    }
}

/** Body paragraph under a headline, in the muted warm-gray used across detail screens. */
@Composable
fun DetailBody(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = PlatformResetColors.TextBodyDetail,
        modifier = modifier,
    )
}

/**
 * The bordered monospace code-snippet block from Detail.dc.html: `#1C170F` background,
 * `#332A1E` 1px border, no corner radius, horizontally scrollable so long lines don't wrap.
 */
@Composable
fun CodeSnippetBlock(code: String, modifier: Modifier = Modifier) {
    SelectionContainer {
        Text(
            text = code,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = PlexMono,
                fontSize = 13.sp,
                lineHeight = 22.sp,
            ),
            color = PlatformResetColors.CodeText,
            modifier = modifier
                .fillMaxWidth()
                .background(PlatformResetColors.CodeBackground)
                .border(1.dp, PlatformResetColors.CodeBorder)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp),
        )
    }
}

/** The "VERIFIED" section label above a list of [VerifiedRow]s. */
@Composable
fun VerifiedSectionLabel(modifier: Modifier = Modifier, text: String = "Verified") {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = PlatformResetColors.TextMonoSecondary,
        modifier = modifier,
    )
}

/** One checkmark + description row inside a detail screen's "Verified" section. */
@Composable
fun VerifiedRow(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CheckMark(modifier = Modifier.padding(top = 1.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = PlatformResetColors.TextBodyDetail,
        )
    }
}

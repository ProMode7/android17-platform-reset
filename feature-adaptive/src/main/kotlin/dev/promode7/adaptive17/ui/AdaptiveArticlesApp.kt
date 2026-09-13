package dev.promode7.adaptive17.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import dev.promode7.adaptive17.data.ArticleRepository

/**
 * Root composable for the sample. This is the piece that actually demonstrates the Android 17
 * migration: instead of assuming a single fixed window shape (the pre-API-37 assumption that
 * `resizeableActivity="false"` used to let you make), it reads the current [WindowSizeClass] and
 * picks a single-pane or two-pane layout every time that size class changes -- including while
 * the activity is running, e.g. resizing a freeform window, folding/unfolding, or entering
 * split-screen.
 *
 * [windowSizeClass] defaults to the real current window via [currentWindowAdaptiveInfoV2], but is
 * a parameter so tests can inject an arbitrary size class without needing a device of that
 * physical size -- see AdaptiveArticlesAppUiTest.kt.
 */
@Composable
fun AdaptiveArticlesApp(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass,
) {
    val layoutMode = pickLayoutMode(windowSizeClass)
    var selectedArticleId by rememberSaveable { mutableStateOf<String?>(null) }

    // No local theme wrapper here (the original standalone repo's Adaptive17Theme is gone): the
    // whole consolidated app is wrapped once in PlatformResetTheme at the :app level instead.
    Surface(modifier = Modifier.fillMaxSize()) {
            when (layoutMode) {
                LayoutMode.TwoPane -> TwoPaneArticles(
                    selectedArticleId = selectedArticleId ?: ArticleRepository.articles.first().id,
                    onArticleSelected = { selectedArticleId = it },
                )

                LayoutMode.SinglePane -> SinglePaneArticles(
                    selectedArticleId = selectedArticleId,
                    onArticleSelected = { selectedArticleId = it },
                    onBack = { selectedArticleId = null },
                )
            }
    }
}

@Composable
private fun TwoPaneArticles(
    selectedArticleId: String,
    onArticleSelected: (String) -> Unit,
) {
    val article = ArticleRepository.articles.first { it.id == selectedArticleId }
    Row(modifier = Modifier.fillMaxSize()) {
        ArticleListScreen(
            selectedArticleId = selectedArticleId,
            onArticleSelected = onArticleSelected,
            modifier = Modifier
                .weight(0.38f)
                .fillMaxHeight(),
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
        )
        ArticleDetailScreen(
            article = article,
            onBack = null,
            modifier = Modifier
                .weight(0.62f)
                .fillMaxHeight(),
        )
    }
}

@Composable
private fun SinglePaneArticles(
    selectedArticleId: String?,
    onArticleSelected: (String) -> Unit,
    onBack: () -> Unit,
) {
    if (selectedArticleId == null) {
        ArticleListScreen(
            selectedArticleId = null,
            onArticleSelected = onArticleSelected,
            modifier = Modifier.fillMaxSize(),
        )
    } else {
        val article = ArticleRepository.articles.first { it.id == selectedArticleId }
        ArticleDetailScreen(
            article = article,
            onBack = onBack,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

package dev.promode7.adaptive17.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.promode7.adaptive17.data.Article
import dev.promode7.adaptive17.data.ArticleRepository

/** The list pane: shown full-screen on compact width, shown as the left pane at medium width+. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreen(
    selectedArticleId: String?,
    onArticleSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Articles") }) },
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(ArticleRepository.articles, key = { it.id }) { article ->
                ArticleRow(
                    article = article,
                    selected = article.id == selectedArticleId,
                    onClick = { onArticleSelected(article.id) },
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ArticleRow(
    article: Article,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val background = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(text = article.title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = article.summary,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

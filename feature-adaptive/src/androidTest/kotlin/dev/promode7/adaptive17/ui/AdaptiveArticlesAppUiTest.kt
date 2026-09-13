package dev.promode7.adaptive17.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.window.core.layout.WindowSizeClass
import dev.promode7.adaptive17.data.ArticleRepository
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented Compose UI tests. These need a connected device or emulator
 * (./gradlew connectedAndroidTest) because they render real Compose UI through
 * androidx.compose.ui.test -- this repo's sandbox has no device/emulator available, so these are
 * written and reviewed for correctness but NOT executed as part of the build verification
 * described in README.md.
 *
 * Rather than resizing an actual window (which would require a specific physical or emulated
 * device), each test injects an explicit [WindowSizeClass] into [AdaptiveArticlesApp], which is
 * the same technique production code should use to preview/test adaptive layouts at arbitrary
 * window sizes without owning every physical form factor.
 */
class AdaptiveArticlesAppUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun compactWidth_showsListFirst_notDetail() {
        composeTestRule.setContent {
            AdaptiveArticlesApp(windowSizeClass = WindowSizeClass.compute(dpWidth = 360f, dpHeight = 800f))
        }

        composeTestRule.onNodeWithText("Articles").assertExists()
        composeTestRule.onNodeWithText(ArticleRepository.articles.first().title).assertExists()
        // The second article's body text is not on screen yet in single-pane mode.
        composeTestRule.onNodeWithText(ArticleRepository.articles[1].body, substring = true).assertDoesNotExist()
    }

    @Test
    fun compactWidth_tappingArticle_navigatesToDetailWithBackButton() {
        composeTestRule.setContent {
            AdaptiveArticlesApp(windowSizeClass = WindowSizeClass.compute(dpWidth = 360f, dpHeight = 800f))
        }

        val firstArticle = ArticleRepository.articles.first()
        composeTestRule.onNodeWithText(firstArticle.title).performClick()

        composeTestRule.onNodeWithText(firstArticle.body, substring = true).assertExists()
        composeTestRule.onNodeWithContentDescription("Back").assertExists()
    }

    @Test
    fun expandedWidth_showsListAndDetailSimultaneously() {
        composeTestRule.setContent {
            AdaptiveArticlesApp(windowSizeClass = WindowSizeClass.compute(dpWidth = 1200f, dpHeight = 800f))
        }

        val firstArticle = ArticleRepository.articles.first()
        // Both the list ("Articles" header) and the first article's full body are visible at the
        // same time -- that is the two-pane layout Android 17 makes worth building.
        composeTestRule.onNodeWithText("Articles").assertExists()
        composeTestRule.onNodeWithText(firstArticle.body, substring = true).assertExists()
        // No back button in two-pane mode: there is nowhere to navigate back from.
        composeTestRule.onNodeWithContentDescription("Back").assertDoesNotExist()
    }

    @Test
    fun expandedWidth_tappingSecondArticle_updatesDetailPaneInPlace() {
        composeTestRule.setContent {
            AdaptiveArticlesApp(windowSizeClass = WindowSizeClass.compute(dpWidth = 1200f, dpHeight = 800f))
        }

        val secondArticle = ArticleRepository.articles[1]
        composeTestRule.onNodeWithText(secondArticle.title).performClick()

        composeTestRule.onNodeWithText(secondArticle.body, substring = true).assertExists()
        // The list is still visible; selecting an article does not navigate away from it.
        composeTestRule.onNodeWithText("Articles").assertExists()
    }
}

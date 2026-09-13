package com.pramodpatel.notesfunctions

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.pramodpatel.notesfunctions.data.NotesRepository
import com.pramodpatel.notesfunctions.ui.MainActivity
import org.junit.After
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI smoke test: adding a note through the UI updates the same
 * [NotesRepository] backing the AppFunctions in [com.pramodpatel.notesfunctions.appfunctions.BaseNotesAppFunctionService],
 * so it appears in the list immediately.
 */
class NotesAppUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @After
    fun tearDown() {
        NotesRepository.clearForTesting()
    }

    @Test
    fun addingNoteFromUi_showsItInList() {
        composeRule.onNodeWithText("Title").performTextInput("Pick up dry cleaning")
        composeRule.onNodeWithText("Add note").performClick()

        composeRule.onNodeWithText("Pick up dry cleaning").assertExists()
    }
}

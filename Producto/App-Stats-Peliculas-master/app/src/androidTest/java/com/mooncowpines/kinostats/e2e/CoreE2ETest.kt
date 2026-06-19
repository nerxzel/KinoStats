package com.mooncowpines.kinostats.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import com.mooncowpines.kinostats.MainActivity
import org.junit.Rule
import org.junit.Test

class CoreE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun e2e_fullMovieAndListManagementFlow() {
        val randomLetters = (1..6).map { ('a'..'z').random() }.joinToString("")
        val username = "kino$randomLetters"
        val password = "Password123!"

        composeTestRule.onNodeWithText("Create Account").performClick()
        composeTestRule.onNodeWithTag("user_name_input").performTextInput(username)
        composeTestRule.onNodeWithTag("email_input").performTextInput("$username@test.com")
        composeTestRule.onNodeWithTag("password_input").performTextInput(password)
        composeTestRule.onNodeWithTag("password_check_input").performTextInput(password)
        Espresso.closeSoftKeyboard()
        composeTestRule.onNodeWithText("Create Account").performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Welcome to KinoStats!").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNode(hasSetTextAction()).performTextInput("Interstellar")
        Espresso.closeSoftKeyboard()
        composeTestRule.onNode(hasSetTextAction()).performImeAction()

        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithTag("movie_result_card").fetchSemanticsNodes().isNotEmpty()
        }

        Thread.sleep(1000)

        composeTestRule.onAllNodesWithTag("movie_result_card")[0]
            .performScrollTo()
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Overview").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("Add Review").performClick()
        composeTestRule.onNodeWithText("Save to List").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Watchlist").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Watchlist").performClick()
        Thread.sleep(1500)


        composeTestRule.onNodeWithContentDescription("Add Review").performClick()
        composeTestRule.onNodeWithText("Add Log").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Save").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Tap to select a date...").performClick()
        Thread.sleep(1000)

        composeTestRule.onNodeWithText("Ok").performClick()
        Thread.sleep(800)

        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Overview").fetchSemanticsNodes().isNotEmpty()
        }

        Espresso.pressBack()

        Espresso.pressBack()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Welcome to KinoStats!").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("List").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("My Lists").fetchSemanticsNodes().isNotEmpty()
        }

        val myCustomList = "Sci-Fi Favorites"
        composeTestRule.onNodeWithContentDescription("Create List").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("list_name_input").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("list_name_input").performTextInput(myCustomList)
        Espresso.closeSoftKeyboard()
        composeTestRule.onNodeWithText("Create").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(myCustomList).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("Logs").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("My Logs").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Interstellar").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Interstellar").assertIsDisplayed()
    }
}
package com.mooncowpines.kinostats.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.mooncowpines.kinostats.MainActivity
import androidx.test.espresso.Espresso
import org.junit.Rule
import org.junit.Test

class AuthE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun e2e_createAccountAndNavigateToHome() {
        val randomLetters = (1..5).map { ('a'..'z').random() }.joinToString("")
        val username = "User$randomLetters"
        val email = "test$randomLetters@kino.com"
        val password = "Password123!"

        composeTestRule.onNodeWithText("Create Account").performClick()

        composeTestRule.onNodeWithTag("user_name_input").performTextInput(username)
        composeTestRule.onNodeWithTag("email_input").performTextInput(email)
        composeTestRule.onNodeWithTag("password_input").performTextInput(password)
        composeTestRule.onNodeWithTag("password_check_input").performTextInput(password)

        Espresso.closeSoftKeyboard()

        composeTestRule.onNodeWithText("Create Account", useUnmergedTree = true)
            .performScrollTo()
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Welcome to KinoStats!").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Welcome to KinoStats!").assertIsDisplayed()
    }
}
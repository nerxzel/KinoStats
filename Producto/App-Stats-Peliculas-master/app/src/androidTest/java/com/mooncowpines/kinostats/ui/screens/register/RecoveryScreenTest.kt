package com.mooncowpines.kinostats.ui.screens.register


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mooncowpines.kinostats.ui.screens.recovery.RecoveryContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.mooncowpines.kinostats.ui.screens.recovery.RecoveryScreenState

@RunWith(AndroidJUnit4::class)
class RecoveryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun cp_05_recoveryFlow_withValidEmail_navigatesToResetScreen_andPassesEmail() {
        var capturedEmail = ""
        var isRecoveryClicked = false

        composeTestRule.setContent {
            var fakeState by remember { mutableStateOf(RecoveryScreenState()) }

            RecoveryContent(
                modifier = Modifier,
                state = fakeState,
                onEmailChange = { newEmail ->
                    fakeState = fakeState.copy(email = newEmail)
                    capturedEmail = newEmail
                },
                onRecoveryClick = { isRecoveryClicked = true },
                onCancelClick = { }
            )
        }

        val testEmail = "ivan_dev@test.cl"

        composeTestRule.onNodeWithText("example@gmail.com").performTextInput(testEmail)
        composeTestRule.onNodeWithText("Send").performClick()

        assertEquals("Component did not update the email.", testEmail, capturedEmail)
        assertTrue("Send button did not trigger the recovery action.", isRecoveryClicked)

    }
}
package com.mooncowpines.kinostats.data.local

import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SessionManagerTest {

    private lateinit var mockPrefs: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor
    private lateinit var sessionManager: SessionManager

    private val ramStorage = mutableMapOf<String, String>()

    @Before
    fun setup() {
        mockEditor = mockk(relaxed = true)
        mockPrefs = mockk()
        sessionManager = SessionManager(mockPrefs)

        ramStorage[SessionManager.AUTH_TOKEN_KEY] = "token_123"
        every { mockPrefs.edit() } returns mockEditor

        every { mockEditor.remove(any()) } answers {
            val keyToRemove = firstArg<String>()
            ramStorage.remove(keyToRemove)
            mockEditor
        }

        every { mockPrefs.getString(any(), any()) } answers {
            val keyToFetch = firstArg<String>()
            ramStorage[keyToFetch]
        }
    }

    @Test
    fun `CP_06 clearSession should strictly remove tokens and ensure null returns`() {

        sessionManager.clearSession()

        val recoveredToken = sessionManager.fetchAuthToken()

        assertNull(
            "Security vulnerability: SessionManager failed to properly purge the auth token.",
            recoveredToken
        )
    }
}
package com.mooncowpines.kinostats.utils

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import io.mockk.*
import org.junit.After
import org.junit.Before
import java.io.File
import java.io.FileOutputStream

class ValidatorsTest {

    @Test
    fun `CP_01 getEmailError with valid email should return null`() {
        val validEmail = "ivan_dev@test.cl"

        val result = getEmailError(validEmail)

        assertNull("System should return null (no error)", result)
    }

    @Test
    fun `CP_02 getEmailError with invalid emails should return error message`() {
        val malformedEmails = listOf(
            "usuario-sin-arroba.com",
            "usuario@.com",
            "@dominio.com"
        )

        malformedEmails.forEach { invalidEmail ->
            val result = getEmailError(invalidEmail)

            assertEquals(
                "Validator failed and let an invalid email format pass: $invalidEmail",
                "Invalid email format",
                result
            )
        }
    }

    @Test
    fun `CP_03 isPassValid with secure password should return true`() {
        val securePass = "KinoStats2026!"

        val result = isPassValid(securePass)

        assertEquals(
            "System rejected a secure password",
            true,
            result
        )
    }

    @Test
    fun `CP_04 isPassValid with insecure passwords should return false`() {
        val insecurePasswords = listOf(
            "123456",
            "sololetras",
            "1234567!"
        )

        insecurePasswords.forEach { invalidPass ->
            val result = isPassValid(invalidPass)

            assertEquals(
                "Password validator failed: an insecure password passed the evaluation: '$invalidPass'",
                false,
                result
            )
        }
    }

    @Test
    fun `CP_10_isRatingValid with exact half step fractions should return true`() {
        val validFractions = listOf(0.5f, 1.0f, 2.5f, 4.5f, 5.0f)

        validFractions.forEach { rating ->
            val result = isRatingValid(rating)

            assertEquals(
                "Mathematical validator rejected a valid fraction: $rating",
                true,
                result
            )
        }
    }

    @Test
    fun `CP_11_isRatingValid with out of bounds and invalid increments should return false`() {
        val invalidRatings = listOf(
            -0.5f,
            5.5f,
            3.7f
        )

        invalidRatings.forEach { invalidRating ->
            val result = isRatingValid(invalidRating)

            assertEquals(
                "The mathematical validator failed: it accepted an invalid score of $invalidRating",
                false,
                result
            )
        }
    }

    @Test
    fun `CP_13_formatTimestampToDateString should accurately convert utc ms to formatted string`() {
        val testTimestamp = java.time.Instant.parse("2026-06-18T00:00:00Z").toEpochMilli()

        val result = formatTimestampToDateString(testTimestamp, java.util.Locale.ENGLISH)

        assertEquals(
            "Date formatter failed or applied a wrong time zone offset.",
            "18 Jun 2026",
            result
        )
    }

    @Test
    fun `cp22_formatTotalMinutes_shouldConvertRawMinutesToHoursAndMinutesString`() {
        val testValue1 = 135
        val testValue2 = 45

        val result1 = formatTotalMinutes(testValue1)
        val result2 = formatTotalMinutes(testValue2)

        assertEquals(
            "The time formatter failed to calculate hours and minutes for > 60 mins.",
            "2h 15m",
            result1
        )

        assertEquals(
            "The time formatter failed to handle values < 60 mins correctly.",
            "0h 45m",
            result2
        )
    }

    @Test
    fun `CP_getUserNameError checks business rules`() {
        assertEquals("User name cannot be empty", getUserNameError(""))
        assertEquals("User name cannot have less than 5 characters", getUserNameError("Kino"))
        assertEquals("User name only can have letters", getUserNameError("Kino123"))
        assertEquals("This user name is reserved", getUserNameError("adminUser"))
        assertNull(getUserNameError("ValidName"))
    }

    @Test
    fun `CP_getCodeError and getCurrentPassError validate empty strings`() {
        assertEquals("This field cannot be empty", getCodeError(""))
        assertNull(getCodeError("123456"))

        assertEquals("This field cannot be empty", getCurrentPassError("   "))
        assertNull(getCurrentPassError("MyOldPass1!"))
    }

    @Test
    fun `CP_isPassMatch validates matching rules`() {
        assertTrue(isPassMatch("pass123", "pass123"))
        assertFalse(isPassMatch("pass123", "wrong"))
        assertFalse(isPassMatch("pass123", "  "))
    }

    @Test
    fun `CP_getWatchDateError and parseSafely handle dates`() {
        assertEquals("The log must have a date", getWatchDateError(null))
        assertNull(getWatchDateError(LocalDate.now()))

        assertNull(parseSafely(null))
        assertNull(parseSafely("invalid-date"))
        assertEquals(LocalDate.of(2026, 7, 3), parseSafely("2026-07-03"))
    }

    @Test
    fun `CP_formatLocalDateToString works like timestamp formatter`() {
        val date = LocalDate.of(2026, 6, 18)
        val result = formatLocalDateToString(date, java.util.Locale.ENGLISH)
        assertEquals("18 Jun 2026", result)
    }


    @Test
    fun `CP_shareWrappedSlide catches exception safely`() {
        val context = mockk<Context>()
        val bitmap = mockk<Bitmap>()

        every { context.cacheDir } throws RuntimeException("Forced error")

        shareWrappedSlide(context, bitmap)

        assertTrue(true)
    }

}
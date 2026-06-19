package com.mooncowpines.kinostats.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class LogModelTest {

    @Test
    fun `cp15_logModel_creationWithOnlyDate_shouldAllowNullRatingAndText`() {
        val testDate = LocalDate.now()

        val minimalLog = com.mooncowpines.kinostats.domain.model.Log(
            id = 1L,
            userId = 1L,
            movieId = 50L,
            movieTitle = "Minimal Test Movie",
            posterUrl = "",
            releaseYear = 2026,
            watchDate = testDate,
            rating = null,
            logText = null
        )

        assertNotNull(
            "The system threw an exception or failed to create the Log object.",
            minimalLog
        )
        assertEquals(
            "The watch date was not correctly assigned.",
            testDate,
            minimalLog.watchDate
        )
        assertNull(
            "Safety violation: Rating field did not tolerate a null value.",
            minimalLog.rating
        )
        assertNull(
            "Safety violation: LogText field did not tolerate a null value.",
            minimalLog.logText
        )
    }
}
package com.mooncowpines.kinostats.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class LogRepositoryIntegrityTest {

    @Test
    fun `cp_16_updateLog_overwritesExistingData_withoutCreatingDuplicates`() = runBlocking {
        val fakeRepo = FakeLogRepository()

        val initialLog = com.mooncowpines.kinostats.domain.model.Log(
            id = 100L, userId = 1L, movieId = 50L,
            movieTitle = "Test Movie", posterUrl = "", releaseYear = 2026,
            watchDate = LocalDate.now(), rating = 3.0f,
            logText = "This is the ORIGINAL review text."
        )
        fakeRepo.logsDB.add(initialLog)

        val newText = "This is the UPDATED review text."
        val newRating = 4.5f

        fakeRepo.updateLog(
            logId = 100L,
            newMovieId = 50L,
            newUserId = 1L,
            newRating = newRating,
            newWatchDate = LocalDate.now(),
            newReviewText = newText
        )

        assertEquals(
            "Data integrity failure: The database created a duplicate entry instead of overwriting.",
            1,
            fakeRepo.logsDB.size
        )

        val updatedLog = fakeRepo.logsDB[0]

        assertEquals(
            "The review text was not overwritten correctly.",
            newText,
            updatedLog.logText
        )

        assertEquals(
            "The rating was not overwritten correctly.",
            newRating,
            updatedLog.rating
        )
    }
}
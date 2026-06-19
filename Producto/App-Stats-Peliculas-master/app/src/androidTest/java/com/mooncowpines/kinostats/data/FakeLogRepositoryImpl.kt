package com.mooncowpines.kinostats.data

import com.mooncowpines.kinostats.domain.model.Log
import com.mooncowpines.kinostats.domain.repository.LogRepository
import java.time.LocalDate

class FakeLogRepository : LogRepository {

    var shouldReturnError: Boolean = false

    val logsDB = mutableListOf<Log>()

    override suspend fun getLogById(logId: Long): Log? {
        if (shouldReturnError) return null
        return logsDB.find { it.id == logId }
    }

    override suspend fun getLogsByUser(userId: Long): List<Log>? {
        if (shouldReturnError) return null
        return logsDB.filter { it.userId == userId }
    }

    override suspend fun getLogsForMovies(movieId: Long): List<Log> {
        if (shouldReturnError) return emptyList()
        return logsDB.filter { it.movieId == movieId }
    }

    override suspend fun saveLog(
        newMovieId: Long, newUserId: Long?, newRating: Float?,
        newWatchDate: LocalDate?, newReviewText: String?
    ): Boolean {
        if (newUserId == null || shouldReturnError) return false

        val newLog = Log(
            id = (logsDB.maxOfOrNull { it.id } ?: 0L) + 1L,
            movieId = newMovieId, userId = newUserId,
            rating = newRating, watchDate = newWatchDate, logText = newReviewText,
            movieTitle = "Mock Movie", posterUrl = "", releaseYear = 2026
        )
        logsDB.add(newLog)
        return true
    }

    override suspend fun updateLog(
        logId: Long, newMovieId: Long, newUserId: Long?,
        newRating: Float?, newWatchDate: LocalDate?, newReviewText: String?
    ): Boolean {
        if (newUserId == null || shouldReturnError) return false

        val index = logsDB.indexOfFirst { it.id == logId }
        if (index != -1) {
            val existingLog = logsDB[index]
            logsDB[index] = existingLog.copy(
                movieId = newMovieId, userId = newUserId,
                rating = newRating, watchDate = newWatchDate, logText = newReviewText
            )
            return true
        }
        return false
    }

    override suspend fun deleteLog(logId: Long): Boolean {
        if (shouldReturnError) return false
        val initialSize = logsDB.size
        logsDB.removeAll { it.id == logId }
        return logsDB.size < initialSize
    }
}
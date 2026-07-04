package com.mooncowpines.kinostats.data.repositoryImpl

import android.util.Log
import com.mooncowpines.kinostats.data.mapper.toDomain
import com.mooncowpines.kinostats.data.remote.LogApi
import com.mooncowpines.kinostats.data.remote.dto.LogDTO
import com.mooncowpines.kinostats.data.remote.dto.LogRequestDTO
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.LocalDate

class LogRepositoryImplTest {

    private lateinit var api: LogApi
    private lateinit var repository: LogRepositoryImpl

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0

        api = mockk()
        repository = LogRepositoryImpl(api)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getLogById returns mapped domain object on success`() = runTest {
        val logId = 1L
        val dto = LogDTO(1L, "2026-07-03", 5.0f, "Review", 101L, 1L, "/path.jpg", 2026, "Movie")

        coEvery { api.getLogById(logId) } returns Response.success(dto)

        val result = repository.getLogById(logId)

        assertNotNull(result)
        assertEquals(1L, result?.id)
        assertEquals("Movie", result?.movieTitle)
    }

    @Test
    fun `getLogsByUser returns mapped list on success`() = runTest {
        val userId = 1L
        val dto = LogDTO(1L, "2026-07-03", 5.0f, null, 101L, 1L, null, 2026, "Movie")
        coEvery { api.getLogsByUserId(userId) } returns Response.success(listOf(dto))

        val result = repository.getLogsByUser(userId)

        assertEquals(1, result?.size)
    }

    @Test
    fun `getLogsForMovies returns empty list`() = runTest {
        val result = repository.getLogsForMovies(101L)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `saveLog returns true on successful API call`() = runTest {
        val date = LocalDate.of(2026, 7, 3)
        coEvery { api.saveLog(any()) } returns Response.success(mockk())

        val result = repository.saveLog(101L, 1L, 4.0f, date, "Good")

        assertTrue(result)
        coVerify { api.saveLog(match { it.userId == 1L && it.filmId == 101L }) }
    }

    @Test
    fun `updateLog returns false if userId is null`() = runTest {
        val result = repository.updateLog(1L, 101L, null, 4.0f, null, "Review")
        assertFalse(result)
    }

    @Test
    fun `deleteLog returns true on 404 error`() = runTest {
        coEvery { api.deleteLog(1L) } returns Response.error(404, "".toResponseBody())

        val result = repository.deleteLog(1L)
        assertTrue(result)
    }
}
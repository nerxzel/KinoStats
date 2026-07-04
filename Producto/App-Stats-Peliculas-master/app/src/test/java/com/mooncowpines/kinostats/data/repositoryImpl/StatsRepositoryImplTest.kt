package com.mooncowpines.kinostats.data.repositoryImpl

import android.util.Log
import com.mooncowpines.kinostats.data.remote.StatsApi
import com.mooncowpines.kinostats.data.remote.dto.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class StatsRepositoryImplTest {

    private lateinit var api: StatsApi
    private lateinit var repository: StatsRepositoryImpl

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0

        api = mockk()
        repository = StatsRepositoryImpl(api)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getUserStats returns null if userId or year is null`() = runTest {
        assertNull(repository.getUserStats(null, 2026, 1))
        assertNull(repository.getUserStats(1L, null, 1))
    }

    @Test
    fun `getUserStats returns mapped domain object on success including mapping logic`() = runTest {
        val genreDto = TypeWatchesDTO(type = "Action", count = 5)
        val emptyGenreDto = TypeWatchesDTO(type = "", count = 1)

        val dto = StatsResponseDTO(
            moviesWatched = 10, minutesWatched = 100, hoursWatched = 2,
            moviesWatchedByGenre = listOf(genreDto, emptyGenreDto),
            moviesWatchedByCountry = listOf(TypeWatchesDTO("USA", 2)),
            topDirectors = listOf(TypeWatchesDTO("Nolan", 1)),
            topActors = listOf(TypeWatchesDTO("Leo", 1)),
            ratingsCount = listOf(RatingsCountDTO(4.5f, 1)),
            moviesWatchedByDecade = listOf(DecadeWatchesDTO(2020, 1))
        )

        coEvery { api.getStats(any()) } returns Response.success(dto)

        val result = repository.getUserStats(1L, 2026, 6)

        assertNotNull(result)
        assertEquals("Action", result?.genres?.get(0)?.label)
        assertEquals("Unknown", result?.genres?.get(1)?.label)

        assertEquals(2, result?.genres?.size)
    }

    @Test
    fun `getWrappedStats returns null if userId or year is null`() = runTest {
        assertNull(repository.getWrappedStats(null, 2026))
        assertNull(repository.getWrappedStats(1L, null))
    }

    @Test
    fun `getWrappedStats handles exceptions and returns null`() = runTest {
        coEvery { api.getWrapped(any()) } throws RuntimeException("Network Error")

        val result = repository.getWrappedStats(1L, 2026)

        assertNull(result)
    }
}
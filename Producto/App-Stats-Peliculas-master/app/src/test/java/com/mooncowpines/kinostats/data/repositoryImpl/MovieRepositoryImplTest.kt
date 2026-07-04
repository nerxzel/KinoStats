package com.mooncowpines.kinostats.data.repositoryImpl

import android.util.Log
import com.mooncowpines.kinostats.data.remote.MovieApi
import com.mooncowpines.kinostats.data.remote.dto.MovieCardDTO
import com.mooncowpines.kinostats.data.remote.dto.MovieDTO
import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class MovieRepositoryImplTest {

    private lateinit var api: MovieApi
    private lateinit var repository: MovieRepositoryImpl

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        api = mockk()
        repository = MovieRepositoryImpl(api)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getMovieById returns null on API error`() = runTest {
        coEvery { api.getMovieDetails(1L) } returns Response.error(404, "".toResponseBody())

        val result = repository.getMovieById(1L)

        assertNull(result)
        verify { Log.e("MOVIE_REPO", any()) }
    }

    @Test
    fun `searchMovies returns empty list on network exception`() = runTest {
        coEvery { api.searchMovies(any()) } throws RuntimeException("Network crash")

        val result = repository.searchMovies("query")

        assertTrue(result.isEmpty())
        verify { Log.e("MOVIE_REPO", "Search Network error", any()) }
    }

    @Test
    fun `searchMovies maps correctly on success`() = runTest {
        val cardDto = MovieCardDTO(
            id = 1L,
            title = "Test Movie",
            yearOfRelease = 2026,
            duration = 120,
            posterPath = "/path.jpg"
        )

        coEvery { api.searchMovies("test") } returns Response.success(listOf(cardDto))

        val result = repository.searchMovies("test")

        assertEquals(1, result.size)
        assertEquals("Test Movie", result[0].title)
    }
}
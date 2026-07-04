package com.mooncowpines.kinostats.data.repository

import android.util.Log
import com.mooncowpines.kinostats.data.remote.HomeApi
import com.mooncowpines.kinostats.data.remote.dto.HomeDTO
import com.mooncowpines.kinostats.data.remote.dto.MovieCardDTO
import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class HomeRepositoryImplTest {

    private lateinit var api: HomeApi
    private lateinit var repository: HomeRepositoryImpl

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        api = mockk()
        repository = HomeRepositoryImpl(api)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getHomeData returns mapped Home object on successful response`() = runTest {
        val userId = 1L

        val movieCardDTO = MovieCardDTO(
            id = 1502L,
            title = "MegaMan X",
            posterPath = "/poster.jpg",
            yearOfRelease = 1993,
            duration = 120
        )

        val homeDTO = HomeDTO(
            watchList = listOf(movieCardDTO),
            lastSeen = movieCardDTO,
            justWatched = listOf(movieCardDTO)
        )

        coEvery { api.getHomeData(userId) } returns Response.success(homeDTO)

        val result = repository.getHomeData(userId)

        assertNotNull(result)
        assertEquals(1, result?.watchlist?.size)
        assertEquals(1, result?.justWatched?.size)

        assertEquals(1502L, result?.lastSeen?.id)
        assertEquals("MegaMan X", result?.lastSeen?.title)
    }

    @Test
    fun `getHomeData returns null and logs error when response body is null`() = runTest {
        coEvery { api.getHomeData(1L) } returns Response.success(null)

        val result = repository.getHomeData(1L)

        assertNull(result)
    }

    @Test
    fun `getHomeData returns null and logs error on HTTP failure`() = runTest {
        val errorBody = "Error".toResponseBody("text/plain".toMediaTypeOrNull())
        coEvery { api.getHomeData(1L) } returns Response.error(404, errorBody)

        val result = repository.getHomeData(1L)

        assertNull(result)
        verify { Log.e("HomeRepository", "Server error: 404") }
    }

    @Test
    fun `getHomeData returns null and logs error on Exception`() = runTest {
        coEvery { api.getHomeData(1L) } throws RuntimeException("Network crash")

        val result = repository.getHomeData(1L)

        assertNull(result)
        verify { Log.e("HomeRepository", "Network error: Network crash") }
    }
}
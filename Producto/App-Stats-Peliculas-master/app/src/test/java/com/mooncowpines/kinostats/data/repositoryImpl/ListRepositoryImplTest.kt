package com.mooncowpines.kinostats.data.repositoryImpl

import android.util.Log
import com.mooncowpines.kinostats.data.remote.ListApi
import com.mooncowpines.kinostats.data.remote.dto.MovieListDTO
import com.mooncowpines.kinostats.domain.model.MovieList
import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class ListRepositoryImplTest {

    private lateinit var api: ListApi
    private lateinit var repository: ListRepositoryImpl

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0

        api = mockk()
        repository = ListRepositoryImpl(api)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getListsByUser returns mapped list on success`() = runTest {
        val userId = 1L
        val dto = MovieListDTO(movieListId = 1L, name = "Favoritas", movieCount = 5, movies = null, isWatchList = false)
        coEvery { api.getListsByUser(userId) } returns Response.success(listOf(dto))

        val result = repository.getListsByUser(userId)

        assertNotNull(result)
        assertEquals(1, result?.size)
        assertEquals("Favoritas", result?.get(0)?.name)
    }

    @Test
    fun `getListsByUser returns null on API error`() = runTest {
        coEvery { api.getListsByUser(1L) } returns Response.error(404, "Not Found".toResponseBody())

        val result = repository.getListsByUser(1L)

        assertNull(result)
        verify { Log.e("ListRepository", any()) }
    }

    @Test
    fun `createList returns true on success`() = runTest {
        coEvery { api.createList(any()) } returns Response.success(mockk())

        val result = repository.createList(1L, "Nueva")

        assertTrue(result)
    }

    @Test
    fun `createList returns false on error`() = runTest {
        val errorBody = "Error".toResponseBody()
        coEvery { api.createList(any()) } returns Response.error(400, errorBody)

        val result = repository.createList(1L, "Nueva")

        assertFalse(result)
    }

    @Test
    fun `deleteList handles success and exception`() = runTest {
        coEvery { api.deleteList(1L) } returns Response.success(null)
        assertTrue(repository.deleteList(1L))

        coEvery { api.deleteList(2L) } throws RuntimeException("Error")
        assertFalse(repository.deleteList(2L))
    }

    @Test
    fun `addFilmToList returns boolean result`() = runTest {
        coEvery { api.addFilmToList(any()) } returns Response.success(Unit)
        assertTrue(repository.addFilmToList(1L, 1L, 1L))

        coEvery { api.addFilmToList(any()) } throws IOException("Net error")
        assertFalse(repository.addFilmToList(1L, 1L, 1L))
    }
}
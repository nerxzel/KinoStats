package com.mooncowpines.kinostats.utils

import android.util.Log
import io.mockk.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ErrorHandlerTest {

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0

        mockkConstructor(JSONObject::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getErrorMessage returns backend message when JSON is valid and contains error key`() {
        every { anyConstructed<JSONObject>().optString("error", "") } returns "Custom backend error"

        val errorBody = """{"error":"Custom backend error"}""".toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<Any>(400, errorBody)

        val result = response.getErrorMessage()

        assertEquals("Custom backend error", result)
    }

    @Test
    fun `getErrorMessage falls back to HTTP 400 when JSON error key is empty`() {
        every { anyConstructed<JSONObject>().optString("error", "") } returns ""

        val errorBody = """{"other_field":"value"}""".toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<Any>(400, errorBody)

        val result = response.getErrorMessage()

        assertEquals("Invalid request", result)
    }

    @Test
    fun `getErrorMessage falls back to HTTP 401 when body is null`() {
        val errorBody = "".toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<Any>(401, errorBody)

        val result = response.getErrorMessage()

        assertEquals("Invalid credentials", result)
    }

    @Test
    fun `getErrorMessage falls back to HTTP 404`() {
        val errorBody = "".toResponseBody("text/plain".toMediaTypeOrNull())
        val response = Response.error<Any>(404, errorBody)

        assertEquals("Resource not found", response.getErrorMessage())
    }

    @Test
    fun `getErrorMessage falls back to HTTP 500`() {
        val errorBody = "".toResponseBody("text/plain".toMediaTypeOrNull())
        val response = Response.error<Any>(500, errorBody)

        assertEquals("Internal server error", response.getErrorMessage())
    }

    @Test
    fun `getErrorMessage falls back to else branch for unknown codes`() {
        val errorBody = "".toResponseBody("text/plain".toMediaTypeOrNull())
        val response = Response.error<Any>(418, errorBody) // I'm a teapot

        assertEquals("An error occurred (418)", response.getErrorMessage())
    }
}
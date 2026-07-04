package com.mooncowpines.kinostats.data.repositoryImpl

import android.util.Log
import com.mooncowpines.kinostats.data.local.SessionManager
import com.mooncowpines.kinostats.data.remote.AuthApi
import com.mooncowpines.kinostats.data.remote.dto.*
import com.mooncowpines.kinostats.domain.model.User
import com.mooncowpines.kinostats.domain.repository.AuthState
import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class AuthRepositoryImplTest {

    private lateinit var api: AuthApi
    private lateinit var sessionManager: SessionManager
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        api = mockk()
        sessionManager = mockk(relaxed = true)

        every { sessionManager.fetchAuthToken() } returns null
        every { sessionManager.fetchUserId() } returns null

        repository = AuthRepositoryImpl(api, sessionManager)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    @Test
    fun `init sets LOGGED_IN when session has token and userId`() = runTest {
        every { sessionManager.fetchAuthToken() } returns "Basic token"
        every { sessionManager.fetchUserId() } returns 1L

        val repoWithSession = AuthRepositoryImpl(api, sessionManager)
        assertEquals(AuthState.LOGGED_IN, repoWithSession.authState.value)
    }

    @Test
    fun `logout clears session, sets LOGGED_OUT and nullifies currentUser`() = runTest {
        repository.logout()

        verify { sessionManager.clearSession() }
        assertEquals(AuthState.LOGGED_OUT, repository.authState.value)
        assertNull(repository.getCurrentUser())
    }


    @Test
    fun `login success with body returns null and sets session`() = runTest {
        val authHeader = Credentials.basic("user", "pass")
        val loginDto = LoginDTO(userId = 1L, username = "user", email = "test@test.com")

        coEvery { api.login(authHeader) } returns Response.success(loginDto)

        val result = repository.login("user", "pass")

        assertNull(result)
        assertEquals(AuthState.LOGGED_IN, repository.authState.value)
        verify { sessionManager.saveAuthToken(authHeader) }
        verify { sessionManager.saveUserId(1L) }
    }

    @Test
    fun `login success with null body returns empty response error`() = runTest {
        val authHeader = Credentials.basic("user", "pass")
        coEvery { api.login(authHeader) } returns Response.success(null)

        val result = repository.login("user", "pass")

        assertEquals("Empty response from server", result)
    }

    @Test
    fun `login HTTP error returns parsed error message`() = runTest {
        val authHeader = Credentials.basic("user", "pass")
        val errorBody = "Error".toResponseBody("text/plain".toMediaTypeOrNull())
        coEvery { api.login(authHeader) } returns Response.error(401, errorBody)

        val result = repository.login("user", "pass")

        assertNotNull(result)
    }

    @Test
    fun `login throws IOException returns Network error`() = runTest {
        coEvery { api.login(any()) } throws IOException("No internet")
        val result = repository.login("user", "pass")
        assertEquals("Network error. Check your connection.", result)
    }

    @Test
    fun `login throws Exception returns Unexpected error`() = runTest {
        coEvery { api.login(any()) } throws RuntimeException("Crash")
        val result = repository.login("user", "pass")
        assertEquals("An unexpected error occurred", result)
    }


    @Test
    fun `getCurrentUser returns user from memory if already loaded`() = runTest {
        val loginDto = LoginDTO(userId = 1L, username = "user", email = "test@test.com")
        coEvery { api.login(any()) } returns Response.success(loginDto)
        repository.login("user", "pass")

        val user = repository.getCurrentUser()
        assertNotNull(user)
        assertEquals(1L, user?.id)
    }

    @Test
    fun `getCurrentUser fetches from API if memory is null but session exists`() = runTest {
        every { sessionManager.fetchUserId() } returns 1L

        val userDto = UserDTO(id = 1L, userName = "user", email = "t@t.com", pass = "pass")
        coEvery { api.getUserById(1L) } returns Response.success(userDto)

        mockkStatic("com.mooncowpines.kinostats.data.mapper.UserMapperKt")
        val domainUser = User(id = 1L, userName = "user", email = "t@t.com")

        val user = repository.getCurrentUser()
        assertNotNull(user)
    }

    @Test
    fun `getUserById returns null on HttpException or generic Exception`() = runTest {
        val errorBody = "Error".toResponseBody("text/plain".toMediaTypeOrNull())
        val responseError = Response.error<Any>(404, errorBody)

        coEvery { api.getUserById(2L) } throws HttpException(responseError)
        assertNull(repository.getUserById(2L))

        coEvery { api.getUserById(3L) } throws RuntimeException()
        assertNull(repository.getUserById(3L))
    }


    @Test
    fun `register success logs in automatically`() = runTest {
        val userDto = UserDTO(userName = "user", email = "e@e.com", pass = "pass")
        coEvery { api.register(any()) } returns Response.success(userDto)

        val loginDto = LoginDTO(userId = 1L, username = "user", email = "e@e.com")
        coEvery { api.login(any()) } returns Response.success(loginDto)

        val result = repository.register("user", "e@e.com", "pass")

        assertNull(result)
        verify { Log.d("REGISTER", any()) }
    }

    @Test
    fun `register handles exceptions correctly`() = runTest {
        coEvery { api.register(any()) } throws IOException()
        assertEquals("Network error. Check your connection.", repository.register("u", "e", "p"))

        coEvery { api.register(any()) } throws RuntimeException()
        assertEquals("An unexpected error occurred", repository.register("u", "e", "p"))
    }

    @Test
    fun `updateUser fails if currentUser or id is null`() = runTest {
        assertEquals("User not found", repository.updateUser("e", "u", "p", null))

        val field = AuthRepositoryImpl::class.java.getDeclaredField("currentUser")
        field.isAccessible = true
        field.set(repository, User(id = null, userName = "user", email = "e@e.com"))

        assertEquals("User ID missing", repository.updateUser("e", "u", "p", null))
    }

    @Test
    fun `updateUser calls updateUserDetails when newPassword is null`() = runTest {
        val field = AuthRepositoryImpl::class.java.getDeclaredField("currentUser")
        field.isAccessible = true
        field.set(repository, User(id = 1L, userName = "user", email = "e@e.com"))

        coEvery { api.updateUserDetails(1L, any()) } returns Response.success(Unit)

        val result = repository.updateUser("new@e.com", "newUser", "oldPass", null)

        assertNull(result)
        verify { sessionManager.saveAuthToken(Credentials.basic("newUser", "oldPass")) }
    }

    @Test
    fun `updateUser calls updateUserPassword when newPassword is provided`() = runTest {
        val field = AuthRepositoryImpl::class.java.getDeclaredField("currentUser")
        field.isAccessible = true
        field.set(repository, User(id = 1L, userName = "user", email = "e@e.com"))

        coEvery { api.updateUserPassword(1L, any()) } returns Response.success(Unit)

        val result = repository.updateUser("e@e.com", "user", "oldPass", "newPass")

        assertNull(result)
        verify { sessionManager.saveAuthToken(Credentials.basic("user", "newPass")) }
    }

    @Test
    fun `sendRecoveryEmail handles success and errors`() = runTest {
        coEvery { api.requestPasswordReset(any()) } returns Response.success(null)
        assertNull(repository.sendRecoveryEmail("test@test.com"))

        coEvery { api.requestPasswordReset(any()) } throws IOException()
        assertEquals("Network error. Check your connection.", repository.sendRecoveryEmail("e@e.com"))
    }

    @Test
    fun `resetPassword handles success and errors`() = runTest {
        coEvery { api.resetPassword(any()) } returns Response.success(null)
        assertNull(repository.resetPassword("e@e.com", "1234", "newPass"))

        coEvery { api.resetPassword(any()) } throws RuntimeException()
        assertEquals("An unexpected error occurred", repository.resetPassword("e", "code", "pass"))
    }
}
package com.mooncowpines.kinostats.data.repositoryImpl

import com.mooncowpines.kinostats.data.local.SessionManager
import com.mooncowpines.kinostats.data.remote.AuthApi
import com.mooncowpines.kinostats.data.remote.dto.*
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.Mockito.mock

class AuthTransportSecurityTest {
    class FakeAuthApiForSecurityAudit : AuthApi {
        var capturedRegisterPayload: UserDTO? = null

        override suspend fun register(user: UserDTO): retrofit2.Response<UserDTO> {
            capturedRegisterPayload = user
            return retrofit2.Response.success(user)
        }

        override suspend fun login(authHeader: String): retrofit2.Response<LoginDTO> {
            val dummyLoginResponse = LoginDTO(userId = 1L, username = "ivan_dev", email = "ivan_dev@test.cl")
            return retrofit2.Response.success(dummyLoginResponse)
        }

        override suspend fun getUserById(id: Long): retrofit2.Response<UserDTO> = TODO()
        override suspend fun updateUserDetails(id: Long, details: UserDetailsUpdateDTO): retrofit2.Response<Unit> = TODO()
        override suspend fun updateUserPassword(id: Long, passwords: UserPasswordUpdateDTO): retrofit2.Response<Unit> = TODO()
        override suspend fun requestPasswordReset(dto: ForgotPasswordDTO): retrofit2.Response<java.lang.Void> = TODO()
        override suspend fun resetPassword(dto: ResetPasswordDTO): retrofit2.Response<java.lang.Void> = TODO()
    }

    @Test
    fun `cp08_register_encapsulatesPassword_toAllowSecureBcryptHashingOnServer`() = runBlocking {

        val fakeApi = FakeAuthApiForSecurityAudit()
        val fakeSession = mockk<SessionManager>(relaxed = true)
        val repository = AuthRepositoryImpl(fakeApi, fakeSession)

        val rawPassword = "SuperSecretPassword123!"
        val testUser = "ivan_dev"
        val testEmail = "ivan_dev@test.cl"

        repository.register(userName = testUser, email = testEmail, pass = rawPassword)

        assertNotNull(
            "Registration payload did not reach the API",
            fakeApi.capturedRegisterPayload
        )

        assertEquals(
            "Security violation: Password was not packaged correctly in the transfer DTO",
            rawPassword,
            fakeApi.capturedRegisterPayload?.pass
        )
    }
}

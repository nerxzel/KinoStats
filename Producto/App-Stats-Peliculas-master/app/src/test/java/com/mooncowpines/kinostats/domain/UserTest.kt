package com.mooncowpines.kinostats.domain

import com.mooncowpines.kinostats.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class UserTest {

    @Test
    fun `test User data class generated methods`() {
        val user1 = User(
            id = 1L,
            userName = "KinoFan",
            email = "kino@test.com"
        )

        val user2 = User(
            id = 1L,
            userName = "KinoFan",
            email = "kino@test.com"
        )
        assertEquals(user1, user2)
        assertEquals(user1.hashCode(), user2.hashCode())

        val user3 = user1.copy(id = 2L, userName = "AnotherUser")
        assertNotEquals(user1, user3)

        val toStringResult = user1.toString()
        assertNotNull(toStringResult)
        assert(toStringResult.contains("KinoFan"))

        assertEquals(1L, user1.id)
        assertEquals("KinoFan", user1.userName)
        assertEquals("kino@test.com", user1.email)
    }
}
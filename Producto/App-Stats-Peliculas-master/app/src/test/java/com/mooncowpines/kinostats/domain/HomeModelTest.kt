package com.mooncowpines.kinostats.domain

import com.mooncowpines.kinostats.domain.model.Home
import com.mooncowpines.kinostats.domain.model.MovieCard
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class HomeTest {

    @Test
    fun `test Home data class properties and generated methods`() {
        val movieCard1 = mockk<MovieCard>()
        val movieCard2 = mockk<MovieCard>()
        val watchlistMock = listOf(movieCard1)
        val justWatchedMock = listOf(movieCard2)

        val home1 = Home(
            watchlist = watchlistMock,
            justWatched = justWatchedMock,
            lastSeen = movieCard1
        )

        assertEquals(watchlistMock, home1.watchlist)
        assertEquals(justWatchedMock, home1.justWatched)
        assertEquals(movieCard1, home1.lastSeen)

        val home2 = home1.copy(lastSeen = null)
        assertNotEquals(home1, home2)
        assertNull(home2.lastSeen)

        assertNotNull(home1.toString())
        assertEquals(home1.hashCode(), home1.hashCode())

        val home3 = Home(watchlistMock, justWatchedMock, movieCard1)
        assertEquals(home1, home3)
    }
}
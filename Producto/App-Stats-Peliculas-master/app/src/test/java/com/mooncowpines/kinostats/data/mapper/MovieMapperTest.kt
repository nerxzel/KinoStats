package com.mooncowpines.kinostats.data.mapper

import com.mooncowpines.kinostats.data.remote.dto.MovieCardDTO
import com.mooncowpines.kinostats.data.remote.dto.MovieDTO
import com.mooncowpines.kinostats.utils.parseSafely
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class MovieMapperTest {

    @Before
    fun setUp() {
        mockkStatic("com.mooncowpines.kinostats.utils.ValidatorsKt")
    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    @Test
    fun `MovieCardDTO toDomain maps correctly with all valid fields`() {
        val dto = MovieCardDTO(
            id = 1L,
            title = "Shrek",
            posterPath = "/shrek.jpg",
            yearOfRelease = 2001,
            duration = 90
        )

        val result = dto.toDomain()

        assertEquals(1L, result.id)
        assertEquals("Shrek", result.title)
        assertEquals("https://image.tmdb.org/t/p/w500/shrek.jpg", result.posterUrl)
        assertEquals(LocalDate.of(2001, 1, 1), result.releaseDate)
        assertEquals(90, result.duration)
    }

    @Test
    fun `MovieCardDTO toDomain handles null fields and fallback values`() {
        val dto = MovieCardDTO(
            id = 2L,
            title = null,
            posterPath = null,
            yearOfRelease = null,
            duration = null
        )

        val result = dto.toDomain()

        assertEquals(2L, result.id)
        assertEquals("Unknown", result.title)
        assertEquals("", result.posterUrl)
        assertNull(result.releaseDate)
        assertNull(result.duration)
    }

    @Test
    fun `MovieCardDTO toDomain catches exception for invalid yearOfRelease`() {
        val dto = MovieCardDTO(
            id = 3L,
            title = "Future Movie",
            posterPath = null,
            yearOfRelease = Int.MAX_VALUE,
            duration = 120
        )

        val result = dto.toDomain()

        assertNull(result.releaseDate)
    }


    @Test
    fun `MovieDTO toDomain maps correctly with all valid fields`() {
        val expectedDate = LocalDate.of(2023, 10, 10)
        every { parseSafely("2023-10-10") } returns expectedDate

        val dto = MovieDTO(
            id = 10L,
            title = "Inception",
            runtime = 148,
            releaseDate = "2023-10-10",
            productionCountries = "USA",
            genres = "Action, Sci-Fi",
            director = "Christopher Nolan",
            actors = "Leonardo DiCaprio",
            backdropPath = "/back.jpg",
            posterPath = "/poster.jpg",
            overview = "A dream within a dream.",
            productionCompany = "Warner Bros"
        )

        val result = dto.toDomain()

        assertEquals(10L, result.id)
        assertEquals("Inception", result.title)
        assertEquals(148, result.duration)
        assertEquals(expectedDate, result.releaseDate)
        assertEquals("USA", result.originCountry)
        assertEquals(listOf("Action", "Sci-Fi"), result.genres)
        assertEquals("Christopher Nolan", result.director)
        assertEquals("Leonardo DiCaprio", result.actors)
        assertEquals("https://image.tmdb.org/t/p/w780/back.jpg", result.backDropUrl)
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", result.posterUrl)
        assertEquals("A dream within a dream.", result.overview)
        assertEquals("Warner Bros", result.productionCompany)
    }

    @Test
    fun `MovieDTO toDomain handles null fields and provides default values`() {
        every { parseSafely(null) } returns null

        val dto = MovieDTO(
            id = 11L,
            title = "Unknown Movie",
            runtime = null,
            releaseDate = null,
            productionCountries = null,
            genres = null,
            director = null,
            actors = null,
            backdropPath = null,
            posterPath = null,
            overview = null,
            productionCompany = null
        )

        val result = dto.toDomain()

        assertEquals(0, result.duration)
        assertNull(result.releaseDate)
        assertEquals("Unknown", result.originCountry)
        assertEquals(emptyList<String>(), result.genres)
        assertEquals("Unknown", result.director)
        assertEquals("No information available", result.actors)
        assertEquals("", result.backDropUrl)
        assertEquals("", result.posterUrl)
        assertEquals("No overview available.", result.overview)
        assertEquals("Unknown", result.productionCompany)
    }
}
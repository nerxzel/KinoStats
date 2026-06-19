package com.mooncowpines.kinostats.data.mapper

import com.mooncowpines.kinostats.data.remote.dto.MovieCardDTO
import com.mooncowpines.kinostats.data.remote.dto.WrapUpDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class WrapUpMapperTest {

    @Test
    fun cp23_wrapUpMapper_shouldCorrectlyMapFirstAndLastMovies() {
        val firstMovieDTO = MovieCardDTO(
            id = 10L,
            title = "The Matrix",
            yearOfRelease = 1999,
            duration = 136,
            posterPath = "/matrix.jpg",
            director = "The Wachowskis"
        )

        val lastMovieDTO = MovieCardDTO(
            id = 99L,
            title = "Inception",
            yearOfRelease = 2010,
            duration = 148,
            posterPath = "/inception.jpg",
            director = "Christopher Nolan"
        )

        val mockResponse = WrapUpDTO(
            moviesWatched = 50,
            minutesWatched = 6000,
            firstWatched = firstMovieDTO,
            lastWatched = lastMovieDTO,
            moviesWatchedByGenre = emptyList(),
            topDirectors = emptyList(),
            topActors = emptyList()
        )

        val domainModel = mockResponse.toDomain()

        assertNotNull(
            "Data loss: First movie was mapped to null",
            domainModel.firstMovie
        )
        assertEquals(
            "Data loss: First movie title mismatch",
            "The Matrix",
            domainModel.firstMovie?.title
        )

        assertNotNull(
            "Data loss: Last movie was mapped to null",
            domainModel.lastMovie
        )
        assertEquals(
            "Data loss: Last movie title mismatch",
            "Inception",
            domainModel.lastMovie?.title
        )
    }

    @Test
    fun cp24_wrapUpMapper_shouldAssignFallbackWhenGenreIsNullOrEmpty() {
        val corruptGenreDTO = com.mooncowpines.kinostats.data.remote.dto.TypeWatchesDTO(
            type = "",
            count = 12
        )

        val mockResponse = WrapUpDTO(
            moviesWatched = 12,
            minutesWatched = 1440,
            firstWatched = null,
            lastWatched = null,
            moviesWatchedByGenre = listOf(corruptGenreDTO),
            topDirectors = emptyList(),
            topActors = emptyList()
        )

        val domainModel = mockResponse.toDomain()

        assertNotNull(
            "The genres list became null.",
            domainModel.genres
        )

        assertEquals(
            "The mapper failed to inject the 'Unknown' fallback string for an invalid genre.",
            "Unknown",
            domainModel.genres.first().label
        )
    }
}
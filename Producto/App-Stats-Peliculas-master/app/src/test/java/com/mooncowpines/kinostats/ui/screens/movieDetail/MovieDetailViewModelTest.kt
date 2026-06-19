package com.mooncowpines.kinostats.ui.screens.movieDetail

import androidx.lifecycle.SavedStateHandle
import com.mooncowpines.kinostats.domain.model.Movie
import com.mooncowpines.kinostats.domain.model.MovieList
import com.mooncowpines.kinostats.domain.model.User
import com.mooncowpines.kinostats.domain.repository.AuthRepository
import com.mooncowpines.kinostats.domain.repository.ListRepository
import com.mooncowpines.kinostats.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun cp31_addFilmToList_onSuccess_shouldUpdateStateWithMessage() {
        val movieRepositoryMock = mockk<MovieRepository>(relaxed = true)
        val listRepositoryMock = mockk<ListRepository>(relaxed = true)
        val authRepositoryMock = mockk<AuthRepository>(relaxed = true)

        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 550L))

        val dummyMovie = Movie(
            id = 550L,
            title = "Fight Club",
            duration = 139,
            releaseDate = LocalDate.of(1999, 10, 15),
            originCountry = "USA",
            genres = listOf("Drama"),
            director = "David Fincher",
            backDropUrl = "/backdrop.jpg",
            posterUrl = "/poster.jpg",
            overview = "First rule of Fight Club...",
            actors = "Brad Pitt, Edward Norton",
            productionCompany = "20th Century Fox"
        )
        coEvery { movieRepositoryMock.getMovieById(550L) } returns dummyMovie

        val dummyUser = mockk<User>(relaxed = true)
        coEvery { dummyUser.id } returns 99L
        coEvery { authRepositoryMock.getCurrentUser() } returns dummyUser

        val dummyList = mockk<MovieList>(relaxed = true)
        coEvery { dummyList.id } returns 1L
        coEvery { dummyList.name } returns "Watchlist"

        coEvery { listRepositoryMock.addFilmToList(userId = 99L, movieListId = 1L, filmId = 550L) } returns true

        val viewModel = MovieDetailViewModel(
            movieRepository = movieRepositoryMock,
            listRepository = listRepositoryMock,
            authRepository = authRepositoryMock,
            savedStateHandle = savedStateHandle
        )

        viewModel.addFilmToList(dummyList)

        val currentState = viewModel.state.value as MovieDetailState.Success

        assertEquals(
            "The UI State did not reflect the success message after adding the film.",
            "Added to Watchlist",
            currentState.listActionMessage
        )
    }


    @Test
    fun cp34_addFilmToList_shouldTriggerVisualFeedbackInState() {
        val movieRepositoryMock = mockk<MovieRepository>(relaxed = true)
        val listRepositoryMock = mockk<ListRepository>(relaxed = true)
        val authRepositoryMock = mockk<AuthRepository>(relaxed = true)
        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 101L))

        coEvery { listRepositoryMock.addFilmToList(any(), any(), any()) } returns true

        val viewModel = MovieDetailViewModel(
            movieRepository = movieRepositoryMock,
            listRepository = listRepositoryMock,
            authRepository = authRepositoryMock,
            savedStateHandle = savedStateHandle
        )

        val dummyList = MovieList(
            id = 202L,
            name = "Favoritas",
            movieCount = 0,
            movies = emptyList(),
            isWatchList = false
        )

        viewModel.addFilmToList(dummyList)

        val currentState = viewModel.state.value
        assert(currentState is MovieDetailState.Success)
        val successState = currentState as MovieDetailState.Success

        assertEquals("Added to Favoritas", successState.listActionMessage)
    }
}
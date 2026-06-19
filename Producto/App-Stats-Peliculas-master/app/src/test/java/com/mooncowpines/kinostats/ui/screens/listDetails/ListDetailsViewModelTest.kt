package com.mooncowpines.kinostats.ui.screens.listDetails

import com.mooncowpines.kinostats.ui.screens.listDetail.ListDetailState
import com.mooncowpines.kinostats.ui.screens.listDetail.ListDetailViewModel

import androidx.lifecycle.SavedStateHandle
import com.mooncowpines.kinostats.domain.model.MovieCard
import com.mooncowpines.kinostats.domain.model.MovieList
import com.mooncowpines.kinostats.domain.repository.ListRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListDetailViewModelTest {

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
    fun cp32_removeMovieFromList_shouldUpdateStateWithEmptyList() {
        val listRepositoryMock = mockk<ListRepository>(relaxed = true)
        val savedStateHandle = SavedStateHandle(mapOf("listId" to 1L))

        val movieToRemove = mockk<MovieCard>(relaxed = true)
        coEvery { movieToRemove.id } returns 550L

        val fullList = mockk<MovieList>(relaxed = true)
        coEvery { fullList.id } returns 1L

        val emptyList = mockk<MovieList>(relaxed = true)
        coEvery { emptyList.movies } returns emptyList()

        coEvery { listRepositoryMock.getListById(1L) } returns fullList andThen emptyList
        coEvery { listRepositoryMock.removeFilmFromList(1L, 550L) } returns true

        val viewModel = ListDetailViewModel(listRepositoryMock, savedStateHandle)

        viewModel.onConfirmRemoveIntent(movieToRemove)

        viewModel.removeMovieFromList()

        val finalState = viewModel.state.value as ListDetailState.Success
        assertTrue("The list should be empty after removal", finalState.movieList.movies.isEmpty())
    }
}
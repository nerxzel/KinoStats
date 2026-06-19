package com.mooncowpines.kinostats.ui.screens.lists

import com.mooncowpines.kinostats.domain.repository.AuthRepository
import com.mooncowpines.kinostats.domain.repository.ListRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListsScreenViewModelTest {

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
    fun cp_28_createList_withEmptyOrBlankName_shouldPreventRepositoryCall() {
        val listRepositoryMock = mockk<ListRepository>(relaxed = true)
        val authRepositoryMock = mockk<AuthRepository>(relaxed = true)

        val viewModel = ListsScreenViewModel(
            listRepository = listRepositoryMock,
            authRepository = authRepositoryMock
        )

        viewModel.onNewListNameChange("")
        viewModel.createList()

        viewModel.onNewListNameChange("   ")
        viewModel.createList()

        coVerify(exactly = 0) {
            listRepositoryMock.createList(any(), any())
        }
    }


    @Test
    fun cp30_createList_withDuplicateName_shouldAllowCreationAndAssignUniqueIds() {
        val listRepositoryMock = mockk<ListRepository>(relaxed = true)
        val authRepositoryMock = mockk<AuthRepository>(relaxed = true)

        val dummyUser = mockk<com.mooncowpines.kinostats.domain.model.User>()
        coEvery { dummyUser.id } returns 100L
        coEvery { authRepositoryMock.getCurrentUser() } returns dummyUser

        val listOne = mockk<com.mooncowpines.kinostats.domain.model.MovieList>()
        coEvery { listOne.id } returns 1L
        coEvery { listOne.name } returns "Mis Favoritas"

        val listTwo = mockk<com.mooncowpines.kinostats.domain.model.MovieList>()
        coEvery { listTwo.id } returns 2L
        coEvery { listTwo.name } returns "Mis Favoritas"

        coEvery {
            listRepositoryMock.getListsByUser(100L)
        } returns listOf(listOne) andThen listOf(listOne, listTwo)

        coEvery { listRepositoryMock.createList(100L, "Mis Favoritas") } returns true

        val viewModel = ListsScreenViewModel(
            listRepository = listRepositoryMock,
            authRepository = authRepositoryMock
        )

        viewModel.onNewListNameChange("Mis Favoritas")
        viewModel.createList()

        val currentLists = viewModel.state.value.lists

        assertEquals(
            "The system blocked the creation of a list with a duplicate name.",
            2,
            currentLists.size
        )

        assertEquals("Name mismatch on first list", "Mis Favoritas", currentLists[0].name)
        assertEquals("Name mismatch on second list", "Mis Favoritas", currentLists[1].name)

        assert(currentLists[0].id != currentLists[1].id) {
            "The IDs of the duplicated lists are not unique."
        }

        coVerify(exactly = 1) { listRepositoryMock.createList(100L, "Mis Favoritas") }
    }

    @Test
    fun cp33_createList_onRepositoryFailure_shouldUpdateStateWithErrorMsg() {
        val listRepositoryMock = mockk<ListRepository>(relaxed = true)
        val authRepositoryMock = mockk<AuthRepository>(relaxed = true)

        val dummyUser = mockk<com.mooncowpines.kinostats.domain.model.User>(relaxed = true)
        coEvery { dummyUser.id } returns 1L
        coEvery { authRepositoryMock.getCurrentUser() } returns dummyUser

        coEvery { listRepositoryMock.createList(any(), any()) } returns false

        val viewModel = ListsScreenViewModel(listRepositoryMock, authRepositoryMock)

        viewModel.onNewListNameChange("Lista Nueva")
        viewModel.createList()

        val currentState = viewModel.state.value
        assertEquals("Failed to create list", currentState.errorMsg)
        assertEquals(false, currentState.isCreating)
    }
}
package com.mooncowpines.kinostats.ui.screens.stats

import com.mooncowpines.kinostats.domain.repository.AuthRepository
import com.mooncowpines.kinostats.domain.repository.StatsRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class StatsScreenViewModelTest {

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
    fun cp_25_updateFilter_shouldModifySelectedYearAndMonthInUiState() {
        val statsRepositoryMock = mockk<StatsRepository>(relaxed = true)
        val authRepositoryMock = mockk<AuthRepository>(relaxed = true)

        val viewModel = StatsScreenViewModel(
            statsRepository = statsRepositoryMock,
            authRepository = authRepositoryMock
        )

        viewModel.updateFilter(year = 2026, month = 6)

        val currentState = viewModel.state.value

        assertEquals(
            "The selected year did not update correctly in the UI State.",
            2026,
            currentState.selectedYear
        )

        assertEquals(
            "The selected month did not update correctly in the UI State.",
            6,
            currentState.selectedMonth
        )
    }

    @Test
    fun cp_26_updateFilter_withNullMonth_shouldExpandScopeToEntireYearInUiState() {
        val statsRepositoryMock = mockk<StatsRepository>(relaxed = true)
        val authRepositoryMock = mockk<AuthRepository>(relaxed = true)

        val viewModel = StatsScreenViewModel(
            statsRepository = statsRepositoryMock,
            authRepository = authRepositoryMock
        )

        viewModel.updateFilter(year = 2026, month = null)

        val currentState = viewModel.state.value

        assertEquals(
            "The filter failed to register the year for the annual scope.",
            2026,
            currentState.selectedYear
        )

        assertEquals(
            "The filter failed to assign a null month for the annual scope.",
            null,
            currentState.selectedMonth
        )
    }
}
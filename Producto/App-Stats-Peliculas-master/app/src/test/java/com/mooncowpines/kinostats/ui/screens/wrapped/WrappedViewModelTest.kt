package com.mooncowpines.kinostats.ui.screens.wrapped

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class WrappedViewModelTest {

    @Test
    fun cp35_wrappedState_containsOnlyStatsData() {
        val state = WrappedScreenState(
            isLoading = false,
            stats = null,
            errorMsg = null
        )

        assertEquals("El estado inicial no debe estar cargando", false, state.isLoading)
        assertEquals("El estado inicial no debe tener errores", null, state.errorMsg)

    }
}
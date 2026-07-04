package com.mooncowpines.kinostats.data.dto

import com.mooncowpines.kinostats.data.remote.dto.DecadeWatchesDTO
import com.mooncowpines.kinostats.data.remote.dto.LogDTO
import com.mooncowpines.kinostats.data.remote.dto.LogRequestDTO
import com.mooncowpines.kinostats.data.remote.dto.RatingsCountDTO
import com.mooncowpines.kinostats.data.remote.dto.StatsRequestDTO
import com.mooncowpines.kinostats.data.remote.dto.StatsResponseDTO
import com.mooncowpines.kinostats.data.remote.dto.TypeWatchesDTO
import com.mooncowpines.kinostats.data.remote.dto.WrapRequestDTO
import org.junit.Assert.*
import org.junit.Test

class DataTransferObjectsTest {

    @Test
    fun `test all DTO structures`() {
        val log = LogDTO(1L, "2026-07-03", 5.0f, "Great", 101L, 1L, "/path.jpg", 2026, "Movie")
        assertEquals(1L, log.id)
        assertNotNull(log.toString())

        val statsReq = StatsRequestDTO(1L, 7, 2026)
        assertEquals(1L, statsReq.userId)

        val statsRes = StatsResponseDTO(
            1,
            100,
            1,
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList()
        )
        assertEquals(1, statsRes.moviesWatched)

        val type = TypeWatchesDTO("Action", 10)
        val rat = RatingsCountDTO(5.0f, 2)
        val dec = DecadeWatchesDTO(1990, 5)
        assertEquals("Action", type.type)
        assertEquals(5.0f, rat.rating)
        assertEquals(1990, dec.decade)

        val logReq = LogRequestDTO("2026-07-03", "Rev", 4.5f, 101L, 1L, true)
        assertTrue(logReq.firstWatch)

        val wrap = WrapRequestDTO(1L, 2026)
        assertEquals(2026, wrap.year)
    }
}
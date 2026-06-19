package com.mooncowpines.kinostats.data.remote

import com.mooncowpines.kinostats.data.remote.dto.StatsRequestDTO
import com.mooncowpines.kinostats.data.remote.dto.StatsResponseDTO
import com.mooncowpines.kinostats.data.remote.dto.WrapRequestDTO
import com.mooncowpines.kinostats.data.remote.dto.WrapUpDTO
import com.mooncowpines.kinostats.domain.model.UserStats
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StatsApi {
    @POST("api/v1/stats/get")
    suspend fun getStats(@Body request: StatsRequestDTO): Response<StatsResponseDTO>

    @POST("api/v1/stats/wrapped")
    suspend fun getWrapped(@Body request: WrapRequestDTO): Response<WrapUpDTO>
}
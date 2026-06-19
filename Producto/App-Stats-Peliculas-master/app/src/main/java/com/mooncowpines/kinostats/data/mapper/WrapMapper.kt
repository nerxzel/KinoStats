package com.mooncowpines.kinostats.data.mapper

import com.mooncowpines.kinostats.data.remote.dto.WrapUpDTO
import com.mooncowpines.kinostats.domain.model.StatItem
import com.mooncowpines.kinostats.domain.model.UserStats

fun WrapUpDTO.toDomain(): UserStats {
    return UserStats(
        totalMovies = this.moviesWatched,
        totalMinutes = this.minutesWatched,
        totalHours = this.minutesWatched / 60,
        firstMovie = this.firstWatched?.toDomain(),
        lastMovie = this.lastWatched?.toDomain(),

        genres = this.moviesWatchedByGenre.map {
            val safeGenres = if (it.type.isNullOrBlank()) "Unknown" else it.type
            StatItem(safeGenres, it.count)
        },
        topDirectors = this.topDirectors.map {
            val safeDirectors = if (it.type.isNullOrBlank()) "Unknown" else it.type
            StatItem(safeDirectors, it.count)
        },
        topActors = this.topActors.map {
            val safeActors = if (it.type.isNullOrBlank()) "Unknown" else it.type
            StatItem(safeActors, it.count)
        },

        countries = emptyList(),
        ratings = emptyList(),
        decades = emptyList()
    )
}
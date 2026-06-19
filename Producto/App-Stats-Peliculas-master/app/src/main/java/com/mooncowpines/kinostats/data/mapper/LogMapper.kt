package com.mooncowpines.kinostats.data.mapper

import com.mooncowpines.kinostats.data.remote.dto.LogDTO
import com.mooncowpines.kinostats.domain.model.Log
import com.mooncowpines.kinostats.utils.parseSafely
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun LogDTO.toDomain(): Log {
    return Log(
        id = this.id,
        movieId = this.filmId,
        userId = this.userId,
        rating = this.rating,
        watchDate = parseSafely(this.date),
        logText = this.review,
        posterUrl = if (this.posterPath != null) "https://image.tmdb.org/t/p/w500${this.posterPath}" else "",
        movieTitle = this.title,
        releaseYear = this.releaseYear ?: 0
    )
}
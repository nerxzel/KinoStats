package com.mooncowpines.kinostats.data.remote.dto

data class WrapUpDTO(
    val moviesWatched: Int,
    val minutesWatched: Int,
    val firstWatched: MovieCardDTO?,
    val lastWatched: MovieCardDTO?,
    val moviesWatchedByGenre: List<TypeWatchesDTO>,
    val topDirectors: List<TypeWatchesDTO>,
    val topActors: List<TypeWatchesDTO>
)
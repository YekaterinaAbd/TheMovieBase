package com.example.kino.data.mapper

import com.example.kino.domain.model.Movie

data class MoviesAnswer(
    val movies: List<Movie>?,
    val dataSource: DataSource,
    val totalPages: Int
)
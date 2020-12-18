package com.example.movies.data.mapper

import com.example.movies.domain.model.Movie

data class MoviesAnswer(
    val movies: List<Movie>?,
    val dataSource: DataSource,
    val totalPages: Int
)
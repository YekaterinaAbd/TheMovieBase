package com.example.movies.domain.model

data class MoviesAnswer(
    val movies: List<Movie>?,
    val dataSource: DataSource,
    val page: Int,
    val totalPages: Int
)

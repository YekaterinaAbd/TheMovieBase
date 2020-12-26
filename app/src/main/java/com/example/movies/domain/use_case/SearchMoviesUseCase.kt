package com.example.movies.domain.use_case

import com.example.movies.data.network.API_KEY
import com.example.movies.domain.repository.MovieRepository

class SearchMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    suspend fun searchMovies(query: String?, page: Int) =
        movieRepository.searchMovies(API_KEY, query, page)
}
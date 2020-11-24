package com.example.kino.domain.use_case

import com.example.kino.data.network.API_KEY
import com.example.kino.domain.repository.MovieRepository

class SearchMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    suspend fun searchMovies(query: String?, page: Int) =
        movieRepository.searchMovies(API_KEY, query, page)
}
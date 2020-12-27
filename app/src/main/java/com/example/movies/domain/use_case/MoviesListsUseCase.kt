package com.example.movies.domain.use_case

import com.example.movies.data.network.API_KEY
import com.example.movies.domain.model.MoviesType
import com.example.movies.domain.repository.MovieRepository

class MoviesListsUseCase(
    private val movieRepository: MovieRepository
) {

    suspend fun getMovies(page: Int, moviesType: MoviesType, sessionId: String, sortBy: String?) =
        movieRepository.getMovies(moviesType, API_KEY, page, sessionId, sortBy)
}

package com.example.kino.domain.use_case

import com.example.kino.data.network.API_KEY
import com.example.kino.domain.repository.MovieRepository

class MoviesListsUseCase(
    private val movieRepository: MovieRepository
) {
    suspend fun getTopMovies(page: Int) = movieRepository.getTopMovies(API_KEY, page)

    suspend fun getCurrentPlaying(page: Int) = movieRepository.getCurrentMovies(API_KEY, page)

    suspend fun getUpcomingMovies(page: Int) = movieRepository.getUpcomingMovies(API_KEY, page)

    suspend fun getFavouriteMovies(sessionId: String) =
        movieRepository.getFavouriteMovies(API_KEY, sessionId)

    suspend fun getWatchListMovies(sessionId: String) =
        movieRepository.getWatchListMovies(API_KEY, sessionId)
}

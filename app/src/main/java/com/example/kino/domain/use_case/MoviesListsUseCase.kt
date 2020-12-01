package com.example.kino.domain.use_case

import com.example.kino.data.model.movie.MoviesType
import com.example.kino.data.network.API_KEY
import com.example.kino.domain.repository.MovieRepository

class MoviesListsUseCase(
    private val movieRepository: MovieRepository
) {

    suspend fun getMovies(page: Int, sessionId: String, moviesType: MoviesType) =
        when (moviesType) {
            MoviesType.TOP -> getTopMovies(page)
            MoviesType.CURRENT -> getCurrentPlaying(page)
            MoviesType.UPCOMING -> getUpcomingMovies(page)
            MoviesType.FAVOURITES -> getFavouriteMovies(sessionId, page)
            MoviesType.WATCH_LIST -> getWatchListMovies(sessionId, page)
        }

    suspend fun getTopMovies(page: Int) = movieRepository.getTopMovies(API_KEY, page)

    suspend fun getCurrentPlaying(page: Int) = movieRepository.getCurrentMovies(API_KEY, page)

    suspend fun getUpcomingMovies(page: Int) = movieRepository.getUpcomingMovies(API_KEY, page)

    suspend fun getFavouriteMovies(sessionId: String, page: Int) =
        movieRepository.getFavouriteMovies(API_KEY, sessionId, page)

    suspend fun getWatchListMovies(sessionId: String, page: Int) =
        movieRepository.getWatchListMovies(API_KEY, sessionId, page)
}

package com.example.movies.domain.use_case

import android.content.Context
import com.example.movies.data.model.movie.MoviesType
import com.example.movies.data.network.API_KEY
import com.example.movies.domain.repository.MovieRepository

class MoviesListsUseCase(
    private val movieRepository: MovieRepository
) {

    suspend fun getMovies(page: Int, moviesType: MoviesType, context: Context) =
        movieRepository.getMovies(moviesType, API_KEY, page, context)

//        when (moviesType) {
//            MoviesType.TOP -> getTopMovies(page)
//            MoviesType.CURRENT -> getCurrentPlaying(page)
//            MoviesType.UPCOMING -> getUpcomingMovies(page)
//            MoviesType.POPULAR -> getPopularMovies(page)
//            MoviesType.FAVOURITES -> getFavouriteMovies(sessionId, page)
//            MoviesType.WATCH_LIST -> getWatchListMovies(sessionId, page)
//        }

//    suspend fun getTopMovies(page: Int) = movieRepository.getTopMovies(API_KEY, page)
//
//    suspend fun getCurrentPlaying(page: Int) = movieRepository.getCurrentMovies(API_KEY, page)
//
//    suspend fun getUpcomingMovies(page: Int) = movieRepository.getUpcomingMovies(API_KEY, page)
//
//    suspend fun getPopularMovies(page: Int) = movieRepository.getPopularMovies(API_KEY, page)
//
//    suspend fun getFavouriteMovies(sessionId: String, page: Int) =
//        movieRepository.getFavouriteMovies(API_KEY, sessionId, page)
//
//    suspend fun getWatchListMovies(sessionId: String, page: Int) =
//        movieRepository.getWatchListMovies(API_KEY, sessionId, page)
}

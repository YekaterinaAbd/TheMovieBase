package com.example.movies.domain.repository

import android.content.Context
import com.example.movies.data.mapper.MoviesAnswer
import com.example.movies.data.model.entities.MovieStatus
import com.example.movies.data.model.movie.*
import com.example.movies.domain.model.Movie

interface MovieRepository {

//    suspend fun getTopMovies(apiKey: String, page: Int): MoviesAnswer
//    suspend fun getCurrentMovies(apiKey: String, page: Int): MoviesAnswer
//    suspend fun getUpcomingMovies(apiKey: String, page: Int): MoviesAnswer
//    suspend fun getPopularMovies(apiKey: String, page: Int): MoviesAnswer
//    suspend fun getFavouriteMovies(apiKey: String, sessionId: String, page: Int): MoviesAnswer
//    suspend fun getWatchListMovies(apiKey: String, sessionId: String, page: Int): MoviesAnswer

    suspend fun getMovies(
        type: MoviesType, apiKey: String, page: Int, context: Context
    ): MoviesAnswer

    suspend fun getRecommendedMovies(apiKey: String, sessionId: String, page: Int): MoviesAnswer?

    suspend fun searchMovies(apiKey: String, query: String?, page: Int): List<Movie>?

    suspend fun updateIsFavourite(movie: FavouriteMovie, sessionId: String): Boolean
    suspend fun synchronizeLocalStatuses(sessionId: String)

    suspend fun updateIsInWatchList(movie: WatchListMovie, sessionId: String): Boolean

    suspend fun insertLocalMovies(movies: List<Movie>)
    suspend fun updateLocalMovieIsFavourite(isFavourite: Boolean, id: Int)
    suspend fun updateLocalMovieIsInWatchList(isInWatchList: Boolean, id: Int)

    suspend fun deleteLocalMovies()

    suspend fun getLocalMovieStatuses(): List<MovieStatus>?
    fun insertLocalMovieStatus(movieState: MovieStatus)
    suspend fun deleteLocalMovieStatus(id: Int)
    fun getLocalSessionId(context: Context): String

    suspend fun getRemoteGenres(apiKey: String): Genres?

    suspend fun getRemoteMovie(id: Int, apiKey: String): MovieDetails?
    suspend fun getTrailer(id: Int, apiKey: String): Video?
    suspend fun getSimilarMovies(id: Int, apiKey: String): List<Movie>?
    suspend fun getKeywords(id: Int, apiKey: String): List<KeyWord>?

    suspend fun getMovieStates(
        movieId: Int,
        apiKey: String,
        sessionId: String
    ): MovieStatus?

    suspend fun updateRemoteFavourites(apiKey: String, sessionId: String, fav: FavouriteMovie)

    suspend fun updateRemoteWatchList(apiKey: String, sessionId: String, fav: WatchListMovie)
    suspend fun getCredits(id: Int, apiKey: String): Credits?

    suspend fun rateMovie(id: Int, apiKey: String, sessionId: String, rating: Double): Boolean

}

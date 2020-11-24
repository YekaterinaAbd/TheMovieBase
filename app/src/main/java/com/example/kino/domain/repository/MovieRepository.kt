package com.example.kino.domain.repository

import android.content.Context
import com.example.kino.data.mapper.DataSource
import com.example.kino.data.model.entities.MovieStatus
import com.example.kino.data.model.movie.*
import com.example.kino.domain.model.Movie

interface MovieRepository {

    suspend fun getTopMovies(apiKey: String, page: Int): Pair<List<Movie>?, DataSource>
    suspend fun getCurrentMovies(apiKey: String, page: Int): Pair<List<Movie>?, DataSource>
    suspend fun getFavouriteMovies(
        apiKey: String,
        sessionId: String
    ): Pair<List<Movie>?, DataSource>

    suspend fun getWatchListMovies(
        apiKey: String,
        sessionId: String
    ): Pair<List<Movie>?, DataSource>

    suspend fun getUpcomingMovies(apiKey: String, page: Int): Pair<List<Movie>?, DataSource>
    suspend fun searchMovies(apiKey: String, query: String?, page: Int): List<Movie>?

    suspend fun updateLikeStatus(movie: FavouriteMovie, sessionId: String): Boolean
    suspend fun loadLocalLikes(sessionId: String)

    suspend fun insertLocalMovies(movies: List<Movie>)
    suspend fun updateLocalMovieIsCLicked(isClicked: Boolean, id: Int)

    suspend fun deleteLocalMovies()

    suspend fun getLocalMovieStatuses(): List<MovieStatus>?
    fun insertLocalMovieStatus(movieState: MovieStatus)
    suspend fun deleteLocalMovieStatus(id: Int)
    fun getLocalSessionId(context: Context): String

    suspend fun getRemoteGenres(apiKey: String): Genres?

    suspend fun getRemoteMovie(id: Int, apiKey: String): RemoteMovieDetails?
    suspend fun getTrailer(id: Int, apiKey: String): Video?
    suspend fun getSimilarMovies(id: Int, apiKey: String): List<Movie>?
    suspend fun getKeywords(id: Int, apiKey: String): List<KeyWord>?

    suspend fun getRemoteMovieList(apiKey: String, page: Int): List<Movie>?
    suspend fun getRemoteFavouriteMovies(apiKey: String, sessionId: String): List<Movie>?
    suspend fun getRemoteMovieStates(movieId: Int, apiKey: String, sessionId: String): Boolean?
    suspend fun updateRemoteFavourites(apiKey: String, sessionId: String, fav: FavouriteMovie)
}

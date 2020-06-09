package com.example.kino.model.repository

import com.example.kino.model.database.MovieDao
import com.example.kino.model.database.MovieStatusDao
import com.example.kino.model.movie.Movie
import com.example.kino.model.movie.MovieStatus
import com.example.kino.model.movie.SelectedMovie
import com.example.kino.utils.PostApi

interface MovieRepository {
    fun getLocalMovies(): List<Movie>?
    fun getLocalFavouriteMovies(): List<Movie>?
    fun insertLocalMovies(movies: List<Movie>)
    fun deleteLocalMovies()
    fun updateLocalMovieIsCLicked(isClicked: Boolean, id: Int)
    fun updateLocalMovieProperties(tagline: String, runtime: Int, id: Int)
    fun getLocalMovie(id: Int): Movie?

    fun insertLocalMovieStatus(movieState:MovieStatus)
    fun getLocalMovieStatuses(): List<MovieStatus>?
    fun deleteLocalMovieStatuses()

    suspend fun getRemoteMovie(id: Int, apiKey: String): Movie?
    suspend fun getRemoteMovieList(apiKey: String): List<Movie>?
    suspend fun getRemoteFavouriteMovies(apiKey: String, sessionId: String): List<Movie>?
    suspend fun addRemoveRemoteFavourites(apiKey: String, sessionId: String, fav: SelectedMovie)
    suspend fun getRemoteMovieStates(movieId: Int, apiKey: String, sessionId: String): MovieStatus?
}

class MovieRepositoryImpl constructor(
    private var movieDao: MovieDao? = null,
    private var service: PostApi? = null,
    private var movieStatusDao: MovieStatusDao? = null
) : MovieRepository {

    override fun getLocalMovies(): List<Movie>? {
        return movieDao?.getMovies()
    }

    override fun getLocalFavouriteMovies(): List<Movie>? {
        return movieDao?.getFavouriteMovies()
    }

    override fun insertLocalMovies(movies: List<Movie>) {
        movieDao?.insertAll(movies)
    }

    override fun deleteLocalMovies() {
        movieDao?.deleteAll()
    }

    override fun updateLocalMovieIsCLicked(isClicked: Boolean, id: Int) {
        movieDao?.updateMovieIsCLicked(isClicked, id)
    }

    override fun updateLocalMovieProperties(tagline: String, runtime: Int, id: Int) {
        movieDao?.updateMovieProperties(tagline, runtime, id)
    }

    override fun getLocalMovie(id: Int): Movie? {
        return movieDao?.getMovie(id)
    }

    override fun insertLocalMovieStatus(movieState: MovieStatus) {
        movieStatusDao?.insertMovieStatus(movieState)
    }

    override fun getLocalMovieStatuses(): List<MovieStatus>? {
        return movieStatusDao?.getMovieStatuses()
    }

    override fun deleteLocalMovieStatuses() {
        movieStatusDao?.deleteAll()
    }

    override suspend fun getRemoteMovie(id: Int, apiKey: String): Movie? =
        service?.getMovieById(id, apiKey)?.body()

    override suspend fun getRemoteMovieList(apiKey: String): List<Movie>? =
        service?.getMovieList(apiKey)?.body()?.movieList

    override suspend fun getRemoteFavouriteMovies(apiKey: String, sessionId: String): List<Movie>? =
        service?.getFavouriteMovies(apiKey, sessionId)?.body()?.movieList

    override suspend fun addRemoveRemoteFavourites(
        apiKey: String,
        sessionId: String,
        fav: SelectedMovie
    ) {
        service?.addRemoveFavourites(apiKey, sessionId, fav)
    }

    override suspend fun getRemoteMovieStates(
        movieId: Int,
        apiKey: String,
        sessionId: String
    ): MovieStatus? {
        return service?.getMovieStates(movieId, apiKey, sessionId)?.body()
    }
}


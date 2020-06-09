package com.example.kino.model.repository

import com.example.kino.model.database.MovieDao
import com.example.kino.model.movie.Movie
import com.example.kino.model.movie.MovieStatus
import com.example.kino.model.movie.SelectedMovie
import com.example.kino.utils.PostApi

interface MovieRepository {
    fun getLocalMovies(): List<Movie>
    fun getLocalFavouriteMovies(): List<Movie>
    fun insertLocalMovies(movies: List<Movie>)
    fun deleteLocalMovies()
    fun updateLocalMovieIsCLicked(isClicked: Boolean, id: Int)
    fun updateLocalMovieProperties(tagline: String, runtime: Int, id: Int)
    fun getLocalMovie(id: Int): Movie

    suspend fun getRemoteMovie(id: Int, apiKey: String): Movie?
    suspend fun getRemoteMovieList(apiKey: String): List<Movie>?
    suspend fun getRemoteFavouriteMovies(apiKey: String, sessionId: String): List<Movie>?
    suspend fun addRemoveRemoteFavourites(apiKey: String, sessionId: String, fav: SelectedMovie)
    suspend fun getMovieStates(movieId: Int, apiKey: String, sessionId: String): MovieStatus?
}

class MovieRepositoryImpl(
    private var movieDao: MovieDao,
    private var service: PostApi
) : MovieRepository {

    override fun getLocalMovies(): List<Movie> {
        return movieDao.getMovies()
    }

    override fun getLocalFavouriteMovies(): List<Movie> {
        return movieDao.getFavouriteMovies()
    }

    override fun insertLocalMovies(movies: List<Movie>) {
        return movieDao.insertAll(movies)
    }

    override fun deleteLocalMovies() {
        movieDao.deleteAll()
    }

    override fun updateLocalMovieIsCLicked(isClicked: Boolean, id: Int) {
        movieDao.updateMovieIsCLicked(isClicked, id)
    }

    override fun updateLocalMovieProperties(tagline: String, runtime: Int, id: Int) {
        movieDao.updateMovieProperties(tagline, runtime, id)
    }

    override fun getLocalMovie(id: Int): Movie {
        return movieDao.getMovie(id)
    }

    override suspend fun getRemoteMovie(id: Int, apiKey: String): Movie? =
        service.getMovieById(id, apiKey).body()

    override suspend fun getRemoteMovieList(apiKey: String): List<Movie>? =
        service.getMovieList(apiKey).body()?.movieList

    override suspend fun getRemoteFavouriteMovies(apiKey: String, sessionId: String): List<Movie>? =
        service.getFavouriteMovies(apiKey, sessionId).body()?.movieList

    override suspend fun addRemoveRemoteFavourites(
        apiKey: String,
        sessionId: String,
        fav: SelectedMovie
    ) {
        service.addRemoveFavourites(apiKey, sessionId, fav)
    }

    override suspend fun getMovieStates(
        movieId: Int,
        apiKey: String,
        sessionId: String
    ): MovieStatus? {
        return service.getMovieStates(movieId, apiKey, sessionId).body()
    }
}


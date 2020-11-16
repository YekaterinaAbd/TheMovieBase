package com.example.kino.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.kino.R
import com.example.kino.data.database.MovieDao
import com.example.kino.data.database.MovieStatusDao
import com.example.kino.data.mapper.LocalMovieMapper
import com.example.kino.data.mapper.RemoteMovieMapper
import com.example.kino.data.model.movie.*
import com.example.kino.data.network.API_KEY
import com.example.kino.data.network.MovieApi
import com.example.kino.domain.model.Movie
import com.example.kino.domain.repository.MovieRepository
import com.example.kino.presentation.utils.constants.MEDIA_TYPE
import com.example.kino.presentation.utils.constants.NULLABLE_VALUE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepositoryImpl(
    private var movieDao: MovieDao? = null,
    private var service: MovieApi,
    private var movieStatusDao: MovieStatusDao? = null,
    private var sharedPreferences: SharedPreferences,
    private val remoteMovieMapper: RemoteMovieMapper,
    private val localMovieMapper: LocalMovieMapper
) : MovieRepository {

    override suspend fun getTopMovies(apiKey: String, page: Int): Pair<List<Movie>?, Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getTopMovies(apiKey, page)
                if (response.isSuccessful) {
                    val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                    return@withContext Pair(list, true)
                } else {
                    val list = movieDao?.getMovies()?.map { localMovieMapper.from(it) }
                    return@withContext Pair(list, false)
                }
            } catch (e: Exception) {
                val list = movieDao?.getMovies()?.map { localMovieMapper.from(it) }
                return@withContext Pair(list, false)
            }
        }

    override suspend fun getCurrentMovies(apiKey: String, page: Int): Pair<List<Movie>?, Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getCurrentMovies(apiKey, page)
                if (response.isSuccessful) {
                    val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                    return@withContext Pair(list, true)
                } else {
                    val list = movieDao?.getMovies()?.map { localMovieMapper.from(it) }
                    return@withContext Pair(list, false)
                }
            } catch (e: Exception) {
                val list = movieDao?.getMovies()?.map { localMovieMapper.from(it) }
                return@withContext Pair(list, false)
            }
        }

    override suspend fun getUpcomingMovies(apiKey: String, page: Int): Pair<List<Movie>?, Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getUpcomingMovies(apiKey, page)
                if (response.isSuccessful) {
                    val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                    return@withContext Pair(list, true)
                } else {
                    val list = movieDao?.getMovies()?.map { localMovieMapper.from(it) }
                    return@withContext Pair(list, false)
                }
            } catch (e: Exception) {
                val list = movieDao?.getMovies()?.map { localMovieMapper.from(it) }
                return@withContext Pair(list, false)
            }
        }

    override suspend fun getFavouriteMovies(
        apiKey: String, sessionId: String
    ): Pair<List<Movie>?, Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = service.getFavouriteMovies(apiKey, sessionId)
            if (response.isSuccessful) {
                val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                return@withContext Pair(list, true)
            } else {
                val list = movieDao?.getFavouriteMovies()?.map { localMovieMapper.from(it) }
                return@withContext Pair(list, false)
            }
        } catch (e: Exception) {
            val list = movieDao?.getFavouriteMovies()?.map { localMovieMapper.from(it) }
            return@withContext Pair(list, false)
        }
    }

    override suspend fun updateLikeStatus(movie: SelectedMovie, sessionId: String) =
        withContext(Dispatchers.IO) {
            try {
                updateRemoteFavourites(API_KEY, sessionId, movie)
                updateLocalMovieIsCLicked(movie.selectedStatus, movie.movieId)

            } catch (e: Exception) {
                updateLocalMovieIsCLicked(movie.selectedStatus, movie.movieId)
                insertLocalMovieStatus(
                    MovieStatus(movie.movieId, movie.selectedStatus)
                )
            }
        }

    override suspend fun loadLocalLikes(sessionId: String) {
        withContext(Dispatchers.IO) {
            val moviesToUpdate = movieStatusDao?.getMovieStatuses()
            if (!moviesToUpdate.isNullOrEmpty()) {
                for (movie in moviesToUpdate) {
                    updateLikeStatus(
                        SelectedMovie(MEDIA_TYPE, movie.movieId, movie.selectedStatus),
                        sessionId
                    )
                }
            }
            deleteLocalMovieStatuses()
        }
    }

    override suspend fun insertLocalMovies(movies: List<Movie>) {
        withContext(Dispatchers.IO) {
            movieDao?.insertAll(movies.map { localMovieMapper.to(it) })
        }
    }

    override suspend fun deleteLocalMovies() {
        withContext(Dispatchers.IO) {
            movieDao?.deleteAll()
        }
    }

    override suspend fun updateLocalMovieIsCLicked(isClicked: Boolean, id: Int) {
        withContext(Dispatchers.IO) {
            movieDao?.updateMovieIsCLicked(isClicked, id)
        }
    }

    override fun updateLocalMovieProperties(tagLine: String, runtime: Int, id: Int) {
        movieDao?.updateMovieProperties(tagLine, runtime, id)
    }

    override fun getLocalMovie(id: Int): Movie? {
        return movieDao?.getMovie(id)?.let { localMovieMapper.from(it) }
    }

    override fun insertLocalMovieStatus(movieState: MovieStatus) {
        movieStatusDao?.insertMovieStatus(movieState)
    }

    override suspend fun getLocalMovieStatuses(): List<MovieStatus>? = withContext(Dispatchers.IO) {
        return@withContext movieStatusDao?.getMovieStatuses()
    }

    override suspend fun deleteLocalMovieStatuses() {
        withContext(Dispatchers.IO) {
            movieStatusDao?.deleteAll()
        }
    }

    override fun getLocalSessionId(context: Context): String {
        return if (sharedPreferences.contains(context.getString(R.string.session_id))) {
            sharedPreferences.getString(
                context.getString(R.string.session_id), NULLABLE_VALUE
            ) as String
        } else NULLABLE_VALUE
    }

    override suspend fun getRemoteGenres(apiKey: String): Genres? {
        return service.getGenres(apiKey).body()
    }

    override suspend fun getRemoteMovie(id: Int, apiKey: String): RemoteMovieDetails? {
        return service.getMovieById(id, apiKey).body()
        //return service.getMovieById(id, apiKey).body()?.let { remoteMovieMapper.from(it) }
    }

    override suspend fun getSimilarMovies(id: Int, apiKey: String): List<Movie>? =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getSimilarMovies(id, apiKey)
                if (response.isSuccessful) {
                    return@withContext response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                } else {
                    return@withContext emptyList<Movie>()
                }
            } catch (e: Exception) {
                return@withContext emptyList<Movie>()

            }
        }

    override suspend fun getKeywords(id: Int, apiKey: String): List<KeyWord>? =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getKeywords(id, apiKey)
                if (response.isSuccessful) {
                    return@withContext response.body()?.keywordsList
                } else {
                    return@withContext emptyList<KeyWord>()
                }
            } catch (e: Exception) {
                return@withContext emptyList<KeyWord>()
            }
        }


    override suspend fun getTrailer(id: Int, apiKey: String): Video? {
        return service.getMovieVideos(id, apiKey).body()?.results?.get(0)
    }

    override suspend fun getRemoteMovieList(apiKey: String, page: Int): List<Movie>? {
        return service.getTopMovies(apiKey, page)
            .body()?.movieList?.map { remoteMovieMapper.from(it) }
    }

    override suspend fun getRemoteFavouriteMovies(
        apiKey: String, sessionId: String
    ): List<Movie>? {
        return service.getFavouriteMovies(apiKey, sessionId)
            .body()?.movieList?.map { remoteMovieMapper.from(it) }
    }

    override suspend fun updateRemoteFavourites(
        apiKey: String,
        sessionId: String,
        fav: SelectedMovie
    ) {
        service.addRemoveFavourites(apiKey, sessionId, fav)
    }

    override suspend fun getRemoteMovieStates(
        movieId: Int, apiKey: String, sessionId: String
    ): Boolean? = withContext(Dispatchers.IO) {
        try {
            val response = service.getMovieStates(movieId, apiKey, sessionId)
            if (response.isSuccessful) return@withContext response.body()?.selectedStatus
            else return@withContext null
        } catch (e: Exception) {
            return@withContext null
        }
    }

    override suspend fun searchMovies(
        apiKey: String, query: String, page: Int
    ): List<Movie>? {
        return try {
            val response = service.searchMovies(apiKey, query, page)
            if (response.isSuccessful) response.body()?.movieList?.map { remoteMovieMapper.from(it) }
            else null
        } catch (e: java.lang.Exception) {
            null
        }
    }
}


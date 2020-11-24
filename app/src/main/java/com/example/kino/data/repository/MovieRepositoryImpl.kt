package com.example.kino.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.kino.R
import com.example.kino.data.database.MovieDao
import com.example.kino.data.database.MovieStatusDao
import com.example.kino.data.mapper.DataSource
import com.example.kino.data.mapper.LocalMovieMapper
import com.example.kino.data.mapper.RemoteMovieMapper
import com.example.kino.data.model.entities.MovieStatus
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

    private fun insertToDatabase(list: List<Movie>?, type: MoviesType) {
        if (!list.isNullOrEmpty()) {
            movieDao?.deleteAll(type.name)
            for (item in list) item.type = type.name
            movieDao?.insertAll(list.map { localMovieMapper.to(it) })
        }
    }

    private fun getFromDatabase(type: MoviesType) =
        movieDao?.getMovies(type.name)?.map { localMovieMapper.from(it) }


    override suspend fun getTopMovies(apiKey: String, page: Int): Pair<List<Movie>?, DataSource> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getTopMovies(apiKey, page)
                if (response.isSuccessful) {
                    val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                    insertToDatabase(list, MoviesType.TOP)
                    return@withContext Pair(list, DataSource.REMOTE)
                } else {
                    val list = getFromDatabase(MoviesType.TOP)
                    return@withContext Pair(list, DataSource.LOCAL)
                }
            } catch (e: Exception) {
                val list = getFromDatabase(MoviesType.TOP)
                return@withContext Pair(list, DataSource.LOCAL)
            }
        }

    override suspend fun getCurrentMovies(
        apiKey: String,
        page: Int
    ): Pair<List<Movie>?, DataSource> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getCurrentMovies(apiKey, page)
                if (response.isSuccessful) {
                    val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                    insertToDatabase(list, MoviesType.CURRENT)
                    return@withContext Pair(list, DataSource.REMOTE)
                } else {
                    val list = getFromDatabase(MoviesType.CURRENT)
                    return@withContext Pair(list, DataSource.LOCAL)
                }
            } catch (e: Exception) {
                val list = getFromDatabase(MoviesType.CURRENT)
                return@withContext Pair(list, DataSource.LOCAL)
            }
        }

    override suspend fun getUpcomingMovies(
        apiKey: String,
        page: Int
    ): Pair<List<Movie>?, DataSource> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getUpcomingMovies(apiKey, page)
                if (response.isSuccessful) {
                    val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                    insertToDatabase(list, MoviesType.UPCOMING)
                    return@withContext Pair(list, DataSource.REMOTE)
                } else {
                    val list = getFromDatabase(MoviesType.UPCOMING)
                    return@withContext Pair(list, DataSource.LOCAL)
                }
            } catch (e: Exception) {
                val list = getFromDatabase(MoviesType.UPCOMING)
                return@withContext Pair(list, DataSource.LOCAL)
            }
        }

    override suspend fun getFavouriteMovies(
        apiKey: String, sessionId: String
    ): Pair<List<Movie>?, DataSource> = withContext(Dispatchers.IO) {
        try {
            val response = service.getFavouriteMovies(apiKey, sessionId)
            if (response.isSuccessful) {
                val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                insertToDatabase(list, MoviesType.FAVOURITES)
                return@withContext Pair(list, DataSource.REMOTE)
            } else {
                val list = getFromDatabase(MoviesType.FAVOURITES)
                return@withContext Pair(list, DataSource.LOCAL)
            }
        } catch (e: Exception) {
            val list = getFromDatabase(MoviesType.FAVOURITES)
            return@withContext Pair(list, DataSource.LOCAL)
        }
    }

    override suspend fun getWatchListMovies(
        apiKey: String,
        sessionId: String
    ): Pair<List<Movie>?, DataSource> = withContext(Dispatchers.IO) {
        try {
            val response = service.getMoviesWatchList(apiKey, sessionId)
            if (response.isSuccessful) {
                val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                insertToDatabase(list, MoviesType.WATCH_LIST)
                return@withContext Pair(list, DataSource.REMOTE)
            } else {
                val list = getFromDatabase(MoviesType.WATCH_LIST)
                return@withContext Pair(list, DataSource.LOCAL)
            }
        } catch (e: Exception) {
            val list = getFromDatabase(MoviesType.WATCH_LIST)
            return@withContext Pair(list, DataSource.LOCAL)
        }
    }

    override suspend fun updateLikeStatus(movie: FavouriteMovie, sessionId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                updateRemoteFavourites(API_KEY, sessionId, movie)
                updateLocalMovieIsCLicked(movie.selectedStatus, movie.movieId)
                return@withContext true

            } catch (e: Exception) {
                updateLocalMovieIsCLicked(movie.selectedStatus, movie.movieId)
                insertLocalMovieStatus(
                    MovieStatus(movie.movieId, movie.selectedStatus)
                )
                if (!movie.selectedStatus) movieDao?.deleteFromFavourites(movie.movieId)
                return@withContext false
            }
        }
    }

    override suspend fun loadLocalLikes(sessionId: String) {
        withContext(Dispatchers.IO) {
            val moviesToUpdate = movieStatusDao?.getMovieStatuses()
            if (!moviesToUpdate.isNullOrEmpty()) {
                for (movie in moviesToUpdate) {
                    val updated = updateLikeStatus(
                        FavouriteMovie(MEDIA_TYPE, movie.movieId, movie.selectedStatus),
                        sessionId
                    )
                    if (updated) deleteLocalMovieStatus(movie.movieId)
                }
            }
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
            if (!isClicked) movieDao?.deleteFromFavourites(id)
        }
    }

    override fun insertLocalMovieStatus(movieState: MovieStatus) {
        movieStatusDao?.insertMovieStatus(movieState)
    }

    override suspend fun getLocalMovieStatuses(): List<MovieStatus>? = withContext(Dispatchers.IO) {
        return@withContext movieStatusDao?.getMovieStatuses()
    }

    override suspend fun deleteLocalMovieStatus(id: Int) {
        withContext(Dispatchers.IO) {
            movieStatusDao?.deleteMovieStatus(id)
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
        fav: FavouriteMovie
    ) {
        service.markFavourite(apiKey, sessionId, fav)
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
        apiKey: String, query: String?, page: Int
    ): List<Movie>? {
        return try {
            val response = service.searchMovies(apiKey, query, page)
            if (response.isSuccessful) {
                response.body()?.movieList?.map { remoteMovieMapper.from(it) }
            } else null
        } catch (e: java.lang.Exception) {
            null
        }
    }
}

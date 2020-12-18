package com.example.movies.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.movies.R
import com.example.movies.data.database.MovieDao
import com.example.movies.data.database.MovieStatusDao
import com.example.movies.data.mapper.DataSource
import com.example.movies.data.mapper.LocalMovieMapper
import com.example.movies.data.mapper.MoviesAnswer
import com.example.movies.data.mapper.RemoteMovieMapper
import com.example.movies.data.model.entities.MovieStatus
import com.example.movies.data.model.movie.*
import com.example.movies.data.network.API_KEY
import com.example.movies.data.network.MovieApi
import com.example.movies.domain.model.Movie
import com.example.movies.domain.repository.MovieRepository
import com.example.movies.presentation.utils.constants.MEDIA_TYPE
import com.example.movies.presentation.utils.constants.NULLABLE_VALUE
import com.google.gson.JsonObject
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


    override suspend fun getTopMovies(
        apiKey: String, page: Int
    ): MoviesAnswer =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getTopMovies(apiKey, page)
                if (response.isSuccessful) {
                    val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                    val totalPages = response.body()?.totalPages ?: 0
                    insertToDatabase(list, MoviesType.TOP)
                    return@withContext MoviesAnswer(list, DataSource.REMOTE, totalPages)
                } else {
                    val list = getFromDatabase(MoviesType.TOP)
                    return@withContext MoviesAnswer(list, DataSource.LOCAL, 1)
                }
            } catch (e: Exception) {
                val list = getFromDatabase(MoviesType.TOP)
                return@withContext MoviesAnswer(list, DataSource.LOCAL, 1)
            }
        }

    override suspend fun getCurrentMovies(
        apiKey: String,
        page: Int
    ): MoviesAnswer =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getCurrentMovies(apiKey, page)
                if (response.isSuccessful) {
                    val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                    val totalPages = response.body()?.totalPages ?: 0
                    insertToDatabase(list, MoviesType.CURRENT)
                    return@withContext MoviesAnswer(list, DataSource.REMOTE, totalPages)
                } else {
                    val list = getFromDatabase(MoviesType.CURRENT)
                    return@withContext MoviesAnswer(list, DataSource.LOCAL, 1)
                }
            } catch (e: Exception) {
                val list = getFromDatabase(MoviesType.CURRENT)
                return@withContext MoviesAnswer(list, DataSource.LOCAL, 1)
            }
        }

    override suspend fun getUpcomingMovies(
        apiKey: String,
        page: Int
    ): MoviesAnswer =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getUpcomingMovies(apiKey, page)
                if (response.isSuccessful) {
                    val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                    val totalPages = response.body()?.totalPages ?: 0
                    insertToDatabase(list, MoviesType.UPCOMING)
                    return@withContext MoviesAnswer(list, DataSource.REMOTE, totalPages)
                } else {
                    val list = getFromDatabase(MoviesType.UPCOMING)
                    return@withContext MoviesAnswer(list, DataSource.LOCAL, 1)
                }
            } catch (e: Exception) {
                val list = getFromDatabase(MoviesType.UPCOMING)
                return@withContext MoviesAnswer(list, DataSource.LOCAL, 1)
            }
        }

    override suspend fun getPopularMovies(apiKey: String, page: Int): MoviesAnswer =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getPopularMovies(apiKey, page)
                if (response.isSuccessful) {
                    val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                    val totalPages = response.body()?.totalPages ?: 0
                    insertToDatabase(list, MoviesType.POPULAR)
                    return@withContext MoviesAnswer(list, DataSource.REMOTE, totalPages)
                } else {
                    val list = getFromDatabase(MoviesType.POPULAR)
                    return@withContext MoviesAnswer(list, DataSource.LOCAL, 1)
                }
            } catch (e: Exception) {
                val list = getFromDatabase(MoviesType.POPULAR)
                return@withContext MoviesAnswer(list, DataSource.LOCAL, 1)
            }
        }

    override suspend fun getFavouriteMovies(
        apiKey: String, sessionId: String, page: Int
    ): MoviesAnswer = withContext(Dispatchers.IO) {
        try {
            val response = service.getFavouriteMovies(apiKey, sessionId, page)
            if (response.isSuccessful) {
                val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                val totalPages = response.body()?.totalPages ?: 0
                insertToDatabase(list, MoviesType.FAVOURITES)
                return@withContext MoviesAnswer(list, DataSource.REMOTE, totalPages)
            } else {
                val list = getFromDatabase(MoviesType.FAVOURITES)
                return@withContext MoviesAnswer(list, DataSource.LOCAL, 1)
            }
        } catch (e: Exception) {
            val list = getFromDatabase(MoviesType.FAVOURITES)
            return@withContext MoviesAnswer(list, DataSource.LOCAL, 1)
        }
    }

    override suspend fun getWatchListMovies(
        apiKey: String, sessionId: String, page: Int
    ): MoviesAnswer = withContext(Dispatchers.IO) {
        try {
            val response = service.getMoviesWatchList(apiKey, sessionId, page)
            if (response.isSuccessful) {
                val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                val totalPages = response.body()?.totalPages ?: 0
                insertToDatabase(list, MoviesType.WATCH_LIST)
                return@withContext MoviesAnswer(list, DataSource.REMOTE, totalPages)
            } else {
                val list = getFromDatabase(MoviesType.WATCH_LIST)
                return@withContext MoviesAnswer(list, DataSource.LOCAL, 1)
            }
        } catch (e: Exception) {
            val list = getFromDatabase(MoviesType.WATCH_LIST)
            return@withContext MoviesAnswer(list, DataSource.LOCAL, 1)
        }
    }

    override suspend fun getRecommendedMovies(
        apiKey: String, sessionId: String, page: Int
    ): MoviesAnswer? = withContext(Dispatchers.IO) {
        try {
            val response = service.getMoviesRecommendations(apiKey, sessionId, page)
            if (response.isSuccessful) {
                val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                val totalPages = response.body()?.totalPages ?: 0
                return@withContext MoviesAnswer(list, DataSource.REMOTE, totalPages)
            } else {
                return@withContext null
            }
        } catch (e: Exception) {
            return@withContext null
        }
    }

    override suspend fun updateIsFavourite(movie: FavouriteMovie, sessionId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                updateRemoteFavourites(API_KEY, sessionId, movie)
                updateLocalMovieIsFavourite(movie.favourite, movie.movieId)
                return@withContext true

            } catch (e: Exception) {
                updateLocalMovieIsFavourite(movie.favourite, movie.movieId)
                insertLocalMovieStatus(
                    MovieStatus(movie.movieId, movie.favourite, false, MoviesType.FAVOURITES.name)
                )
                if (!movie.favourite) movieDao?.deleteFromFavourites(movie.movieId)
                return@withContext false
            }
        }
    }

    override suspend fun updateIsInWatchList(movie: WatchListMovie, sessionId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                updateRemoteWatchList(API_KEY, sessionId, movie)
                updateLocalMovieIsInWatchList(movie.watchlist, movie.movieId)
                return@withContext true

            } catch (e: Exception) {
                updateLocalMovieIsInWatchList(movie.watchlist, movie.movieId)
                insertLocalMovieStatus(
                    MovieStatus(movie.movieId, false, movie.watchlist, MoviesType.WATCH_LIST.name)
                )
                if (!movie.watchlist) movieDao?.deleteFromWatchList(movie.movieId)
                return@withContext false
            }
        }
    }

    override suspend fun synchronizeLocalStatuses(sessionId: String) {
        withContext(Dispatchers.IO) {
            val moviesToUpdate = movieStatusDao?.getMovieStatuses()
            var favouriteUpdated = false
            var watchListUpdated = false
            if (!moviesToUpdate.isNullOrEmpty()) {
                for (movie in moviesToUpdate) {
                    if (movie.type == MoviesType.FAVOURITES.name) {
                        favouriteUpdated = updateIsFavourite(
                            FavouriteMovie(MEDIA_TYPE, movie.movieId, movie.favourite), sessionId
                        )
                    } else {
                        watchListUpdated = updateIsInWatchList(
                            WatchListMovie(MEDIA_TYPE, movie.movieId, movie.watchlist), sessionId
                        )
                    }
                    if (favouriteUpdated && watchListUpdated) deleteLocalMovieStatus(
                        movie.movieId
                    )
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

    override suspend fun updateLocalMovieIsFavourite(isFavourite: Boolean, id: Int) {
        withContext(Dispatchers.IO) {
            movieDao?.updateMovieIsFavourite(isFavourite, id)
            if (!isFavourite) movieDao?.deleteFromFavourites(id)
        }
    }

    override suspend fun updateLocalMovieIsInWatchList(isInWatchList: Boolean, id: Int) {
        withContext(Dispatchers.IO) {
            movieDao?.updateMovieIsInWatchList(isInWatchList, id)
            if (!isInWatchList) movieDao?.deleteFromWatchList(id)
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

    override suspend fun getRemoteMovie(id: Int, apiKey: String): MovieDetails? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = service.getMovieById(id, apiKey)
                if (response.isSuccessful) response.body()
                else null
            } catch (e: Exception) {
                null
            }
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

    override suspend fun getTrailer(id: Int, apiKey: String): Video? =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getMovieVideos(id, apiKey)
                if (response.isSuccessful) return@withContext response.body()?.results?.get(0)
                else return@withContext null
            } catch (e: Exception) {
                return@withContext null
            }
        }

    override suspend fun getCredits(id: Int, apiKey: String): Credits? =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getMovieCredits(id, apiKey)
                if (response.isSuccessful) return@withContext response.body()
                else return@withContext null
            } catch (e: Exception) {
                return@withContext null
            }
        }

    override suspend fun updateRemoteFavourites(
        apiKey: String,
        sessionId: String,
        fav: FavouriteMovie
    ) {
        service.markFavourite(apiKey, sessionId, fav)
    }

    override suspend fun updateRemoteWatchList(
        apiKey: String,
        sessionId: String,
        fav: WatchListMovie
    ) {
        service.markWatchList(apiKey, sessionId, fav)
    }

    override suspend fun getMovieStates(
        movieId: Int, apiKey: String, sessionId: String
    ): MovieStatus? = withContext(Dispatchers.IO) {
        try {
            val response = service.getMovieStates(movieId, apiKey, sessionId)
            if (response.isSuccessful) return@withContext response.body()
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

    override suspend fun rateMovie(
        id: Int,
        apiKey: String,
        sessionId: String,
        rating: Double
    ): Boolean {
        val body = JsonObject().apply {
            addProperty("value", rating)
        }
        return try {
            service.rateMovie(id, apiKey, sessionId, body).isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}

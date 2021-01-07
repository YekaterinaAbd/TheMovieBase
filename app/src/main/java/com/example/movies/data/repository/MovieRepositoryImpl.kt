package com.example.movies.data.repository

import com.example.movies.data.database.MovieDao
import com.example.movies.data.database.MovieStatusDao
import com.example.movies.data.database.RecentMovieDao
import com.example.movies.data.mapper.LocalMovieMapper
import com.example.movies.data.mapper.RemoteMovieMapper
import com.example.movies.data.model.entities.MovieStatus
import com.example.movies.data.model.entities.RecentMovie
import com.example.movies.data.model.movie.*
import com.example.movies.data.model.movie.response.Movies
import com.example.movies.data.network.API_KEY
import com.example.movies.data.network.MovieApi
import com.example.movies.domain.model.DataSource
import com.example.movies.domain.model.Movie
import com.example.movies.domain.model.MoviesAnswer
import com.example.movies.domain.model.MoviesType
import com.example.movies.domain.model.MoviesType.*
import com.example.movies.domain.repository.MovieRepository
import com.example.movies.presentation.utils.constants.MEDIA_TYPE
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class MovieRepositoryImpl(
    private val api: MovieApi,
    private val movieDao: MovieDao,
    private val movieStatusDao: MovieStatusDao,
    private val recentMovieDao: RecentMovieDao,
    private val remoteMovieMapper: RemoteMovieMapper,
    private val localMovieMapper: LocalMovieMapper
) : MovieRepository {

    private fun insertToDatabase(list: List<Movie>?, type: MoviesType) {
        if (!list.isNullOrEmpty()) {
            movieDao.deleteAll(type.name)
            for (item in list) item.type = type.name
            movieDao.insertAll(list.map { localMovieMapper.to(it) })
        }
    }

    private fun getFromDatabase(type: MoviesType) =
        movieDao.getMovies(type.name).map { localMovieMapper.from(it) }
            //тут!!
    private suspend fun getMoviesByType(
        type: MoviesType, apiKey: String, page: Int, sessionId: String, sortBy: String?
    ): Response<Movies> {
        return when (type) {
            TOP -> api.getTopMovies(apiKey, page)
            CURRENT -> api.getCurrentMovies(apiKey, page)
            FAVOURITES -> api.getFavouriteMovies(apiKey, sessionId, page, sortBy)
            POPULAR -> api.getPopularMovies(apiKey, page)
            UPCOMING -> api.getUpcomingMovies(apiKey, page)
            WATCH_LIST -> api.getMoviesWatchList(apiKey, sessionId, page, sortBy)
            RATED -> api.getRatedMovies(apiKey, sessionId, page)
        }
    }

    override suspend fun getMovies(
        type: MoviesType, apiKey: String, page: Int, sessionId: String, sortBy: String?
    ): MoviesAnswer = withContext(Dispatchers.IO) {
        try {
            val response = getMoviesByType(type, apiKey, page, sessionId, sortBy)
            if (response.isSuccessful) {
                val list = response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                val totalPages = response.body()?.totalPages ?: 0
                insertToDatabase(list, type)
                return@withContext MoviesAnswer(list, DataSource.REMOTE, page, totalPages)
            } else {
                val list = getFromDatabase(type)
                return@withContext MoviesAnswer(list, DataSource.LOCAL, page, 1)
            }
        } catch (e: Exception) {
            val list = getFromDatabase(type)
            return@withContext MoviesAnswer(list, DataSource.LOCAL, page, 1)
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
                    MovieStatus(movie.movieId, movie.favourite, false, FAVOURITES.name)
                )
                if (!movie.favourite) movieDao.deleteFromFavourites(movie.movieId)
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
                    MovieStatus(movie.movieId, false, movie.watchlist, WATCH_LIST.name)
                )
                if (!movie.watchlist) movieDao.deleteFromWatchList(movie.movieId)
                return@withContext false
            }
        }
    }

    override suspend fun synchronizeLocalStatuses(sessionId: String) {
        withContext(Dispatchers.IO) {
            val moviesToUpdate = movieStatusDao.getMovieStatuses()
            var favouriteUpdated = false
            var watchListUpdated = false
            if (!moviesToUpdate.isNullOrEmpty()) {
                for (movie in moviesToUpdate) {
                    if (movie.type == FAVOURITES.name) {
                        favouriteUpdated = updateIsFavourite(
                            FavouriteMovie(MEDIA_TYPE, movie.id, movie.favourite), sessionId
                        )
                    } else {
                        watchListUpdated = updateIsInWatchList(
                            WatchListMovie(MEDIA_TYPE, movie.id, movie.watchlist), sessionId
                        )
                    }
                    if (favouriteUpdated && watchListUpdated) deleteLocalMovieStatus(
                        movie.id
                    )
                }
            }
        }
    }

    override suspend fun insertLocalMovies(movies: List<Movie>) {
        withContext(Dispatchers.IO) {
            movieDao.insertAll(movies.map { localMovieMapper.to(it) })
        }
    }

    override suspend fun deleteLocalMovies() {
        withContext(Dispatchers.IO) {
            movieDao.deleteAll()
        }
    }

    override suspend fun updateLocalMovieIsFavourite(isFavourite: Boolean, id: Int) {
        withContext(Dispatchers.IO) {
            movieDao.updateMovieIsFavourite(isFavourite, id)
            if (!isFavourite) movieDao.deleteFromFavourites(id)
        }
    }

    override suspend fun updateLocalMovieIsInWatchList(isInWatchList: Boolean, id: Int) {
        withContext(Dispatchers.IO) {
            movieDao.updateMovieIsInWatchList(isInWatchList, id)
            if (!isInWatchList) movieDao.deleteFromWatchList(id)
        }
    }

    override fun insertLocalMovieStatus(movieState: MovieStatus) {
        movieStatusDao.insertMovieStatus(movieState)
    }

    override suspend fun getLocalMovieStatuses(): List<MovieStatus> = withContext(Dispatchers.IO) {
        return@withContext movieStatusDao.getMovieStatuses()
    }

    override suspend fun deleteLocalMovieStatus(id: Int) {
        withContext(Dispatchers.IO) {
            movieStatusDao.deleteMovieStatus(id)
        }
    }

    override suspend fun getRemoteGenres(apiKey: String): Genres? {
        return api.getGenres(apiKey).body()
    }

    //тут!!
    override suspend fun getRemoteMovie(id: Int, apiKey: String): MovieDetails? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = api.getMovieById(id, apiKey)
                if (response.isSuccessful) {
                    val movie = response.body()
                    if (movie != null) insertRecentMovie(movie)
                    movie
                } else null
            } catch (e: Exception) {
                null
            }
        }

    private fun insertRecentMovie(movie: MovieDetails) {
        val listSize = recentMovieDao.getRecentMoviesCount()
        if (listSize >= 15) recentMovieDao.deleteFirst()
        recentMovieDao.insertRecentMovie(
            RecentMovie(
                id = movie.id,
                title = movie.title,
                releaseDate = movie.releaseDate,
                posterPath = movie.posterPath,
                voteAverage = movie.voteAverage
            )
        )
    }

    override suspend fun getSimilarMovies(id: Int, apiKey: String): List<Movie>? =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getSimilarMovies(id, apiKey)
                if (response.isSuccessful) {
                    return@withContext response.body()?.movieList?.map { remoteMovieMapper.from(it) }
                } else {
                    return@withContext emptyList<Movie>()
                }
            } catch (e: Exception) {
                return@withContext emptyList<Movie>()

            }
        }

    override suspend fun getKeywords(id: Int, apiKey: String): List<Keyword>? =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getMovieKeywords(id, apiKey)
                if (response.isSuccessful) {
                    return@withContext response.body()?.keywordsList
                } else {
                    return@withContext emptyList<Keyword>()
                }
            } catch (e: Exception) {
                return@withContext emptyList<Keyword>()
            }
        }

    override suspend fun getTrailer(id: Int, apiKey: String): Video? =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getMovieVideos(id, apiKey)
                if (response.isSuccessful) return@withContext response.body()?.results?.get(0)
                else return@withContext null
            } catch (e: Exception) {
                return@withContext null
            }
        }

    override suspend fun getCredits(id: Int, apiKey: String): Credits? =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getMovieCredits(id, apiKey)
                if (response.isSuccessful) return@withContext response.body()
                else return@withContext null
            } catch (e: Exception) {
                return@withContext null
            }
        }

    override suspend fun updateRemoteFavourites(
        apiKey: String, sessionId: String, fav: FavouriteMovie
    ) {
        api.markMovieFavourite(apiKey, sessionId, fav)
    }

    override suspend fun updateRemoteWatchList(
        apiKey: String, sessionId: String, fav: WatchListMovie
    ) {
        api.markMovieInWatchList(apiKey, sessionId, fav)
    }

    override suspend fun getMovieStates(
        movieId: Int, apiKey: String, sessionId: String
    ): MovieStatus? {
        return try {
            val response = api.getMovieStates(movieId, apiKey, sessionId)
            if (response.isSuccessful) response.body()
            else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun searchMovies(
        apiKey: String, query: String?, page: Int
    ): List<Movie>? {
        return try {
            val response = api.searchMovies(apiKey, query, page)
            if (response.isSuccessful) {
                response.body()?.movieList?.map { remoteMovieMapper.from(it) }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun discoverMovies(
        apiKey: String, page: Int, genres: String?, keywords: String?
    ): List<Movie>? {
        return try {
            val response = api.discoverMovies(apiKey, page, genres, keywords)
            if (response.isSuccessful) {
                response.body()?.movieList?.map { remoteMovieMapper.from(it) }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun rateMovie(
        id: Int, apiKey: String, sessionId: String, rating: Double
    ): Boolean {
        val body = JsonObject().apply {
            addProperty("value", rating)
        }
        return try {
            api.rateMovie(id, apiKey, sessionId, body).isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}

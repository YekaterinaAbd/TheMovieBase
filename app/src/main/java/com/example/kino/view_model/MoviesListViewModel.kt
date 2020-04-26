package com.example.kino.view_model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.R
import com.example.kino.utils.Constants
import com.example.kino.utils.FragmentEnum
import com.example.kino.utils.RetrofitService
import com.example.kino.model.database.MovieDao
import com.example.kino.model.database.MovieDatabase
import com.example.kino.model.database.MovieStatusDao
import com.example.kino.model.movie.GenresList
import com.example.kino.model.movie.Movie
import com.example.kino.model.movie.MovieStatus
import com.example.kino.model.movie.SelectedMovie
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MoviesListViewModel(
    private val context: Context
) : ViewModel(), CoroutineScope {

    private var movieDao: MovieDao = MovieDatabase.getDatabase(context = context).movieDao()
    private var movieStatusDao: MovieStatusDao =
        MovieDatabase.getDatabase(context = context).movieStatusDao()

    private val constants: Constants = Constants()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sessionId: String

    val liveData = MutableLiveData<State>()
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    init {
        GenresList.getGenres()
        getSharedPreferences()
    }

    private fun getSharedPreferences() {

        sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file),
            Context.MODE_PRIVATE
        )
        if (sharedPref.contains(context.getString(R.string.session_id))) {
            sessionId = sharedPref.getString(
                context.getString(R.string.session_id),
                constants.nullableValue
            ) as String
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getMovies(type: FragmentEnum) {
        launch {
            liveData.value = State.ShowLoading

            val moviesList = withContext(Dispatchers.IO) {
                try {
                    updateFavourites()
                    when (type) {
                        FragmentEnum.TOP -> return@withContext getTop()
                        FragmentEnum.FAVOURITES -> return@withContext getFavourites()
                    }

                } catch (e: Exception) {
                    when (type) {
                        FragmentEnum.TOP -> return@withContext movieDao.getMovies()
                        FragmentEnum.FAVOURITES -> return@withContext movieDao.getFavouriteMovies()
                    }
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(moviesList)
        }
    }

    private fun updateFavourites() {
        val moviesToUpdate = movieStatusDao.getMovieStatuses()
        if (!moviesToUpdate.isNullOrEmpty()) {
            for (movie in moviesToUpdate) {
                val selectedMovie = SelectedMovie(
                    movieId = movie.movieId,
                    selectedStatus = movie.selectedStatus
                )
                addRemoveFavourites(selectedMovie)
            }
        }
        movieStatusDao.deleteAll()
    }

    private suspend fun getTop(): List<Movie>? {
        val response = RetrofitService.getPostApi().getMovieList(constants.apiKey)
        return if (response.isSuccessful) {
            val movies = response.body()?.movieList

            if (!movies.isNullOrEmpty()) {
                for (movie in movies) {
                    setMovieGenres(movie)
                    saveLikeStatus(movie)
                }
                movieDao.deleteAll()
                movieDao.insertAll(movies)
            }
            movies
        } else {
            movieDao.getMovies()
        }
    }

    private suspend fun getFavourites(): List<Movie>? {
        val response = RetrofitService.getPostApi().getFavouriteMovies(constants.apiKey, sessionId)
        return if (response.isSuccessful) {
            val movies = response.body()?.movieList

            if (!movies.isNullOrEmpty()) {
                for (movie in movies) {
                    setMovieGenres(movie)
                    movie.isClicked = true
                }
            }
            movies
        } else {
            movieDao.getFavouriteMovies()
        }
    }

    private fun setMovieGenres(movie: Movie) {
        movie.genreNames = ""
        movie.genreIds?.forEach { genreId ->
            val genreName = GenresList.genres?.get(genreId)
                .toString().toLowerCase(Locale.ROOT)
            movie.genreNames += context.getString(R.string.genre_name, genreName)
        }
    }

    fun addToFavourites(item: Movie) {
        lateinit var selectedMovie: SelectedMovie

        if (!item.isClicked) {
            item.isClicked = true
            selectedMovie = SelectedMovie(constants.mediaType, item.id, item.isClicked)
        } else {
            item.isClicked = false
            selectedMovie = SelectedMovie(constants.mediaType, item.id, item.isClicked)
        }
        addRemoveFavourites(selectedMovie)
    }

    private fun addRemoveFavourites(selectedMovie: SelectedMovie) {
        launch {
            try {
                val response = RetrofitService.getPostApi()
                    .addRemoveFavourites(constants.apiKey, sessionId, selectedMovie)
                if (response.isSuccessful) {
                }
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    movieDao.updateMovieIsCLicked(
                        selectedMovie.selectedStatus,
                        selectedMovie.movieId
                    )
                    val movieStatus =
                        MovieStatus(selectedMovie.movieId, selectedMovie.selectedStatus)
                    movieStatusDao.insertMovieStatus(movieStatus)
                }
            }
        }
    }

    private fun saveLikeStatus(movie: Movie) {
        launch {
            try {
                val response =
                    RetrofitService.getPostApi()
                        .getMovieStates(movie.id, constants.apiKey, sessionId)
                if (response.isSuccessful) {
                    val movieStatus = response.body()
                    if (movieStatus != null) {
                        movie.isClicked = movieStatus.selectedStatus
                        withContext(Dispatchers.IO) {
                            movieDao.updateMovieIsCLicked(movie.isClicked, movie.id)
                        }
                        liveData.value = State.Update
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    sealed class State {
        object Update : State()
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val moviesList: List<Movie>?) : State()
    }
}
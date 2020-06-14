package com.example.kino.view_model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.example.kino.R
import com.example.kino.model.movie.GenresList
import com.example.kino.model.movie.Movie
import com.example.kino.model.movie.MovieStatus
import com.example.kino.model.movie.SelectedMovie
import com.example.kino.model.repository.MovieRepository
import com.example.kino.utils.FragmentEnum
import com.example.kino.utils.constants.API_KEY
import com.example.kino.utils.constants.MEDIA_TYPE
import com.example.kino.utils.constants.NULLABLE_VALUE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MoviesListViewModel(
    private val context: Context,
    private var movieRepository: MovieRepository
) : BaseViewModel() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var sessionId: String

    val liveData = MutableLiveData<State>()

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
                NULLABLE_VALUE
            ) as String
        }
    }

    fun getMovies(type: FragmentEnum, page: Int = 1) {
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            val moviesList = withContext(Dispatchers.IO) {
                try {
                    updateFavourites()
                    when (type) {
                        FragmentEnum.TOP -> return@withContext getTop(page)
                        FragmentEnum.FAVOURITES -> return@withContext getFavourites()
                    }
                } catch (e: Exception) {
                    when (type) {
                        FragmentEnum.TOP -> return@withContext movieRepository.getLocalMovies()
                        FragmentEnum.FAVOURITES -> return@withContext movieRepository.getLocalFavouriteMovies()
                    }
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(moviesList)
        }
    }

    private fun updateFavourites() {
        val moviesToUpdate = movieRepository.getLocalMovieStatuses()

        if (!moviesToUpdate.isNullOrEmpty()) {
            for (movie in moviesToUpdate) {
                updateFavourites(
                    SelectedMovie(movieId = movie.movieId, selectedStatus = movie.selectedStatus)
                )
            }
        }
        movieRepository.deleteLocalMovieStatuses()
    }

    private suspend fun getTop(page: Int): List<Movie>? {
        return try {
            val movies = movieRepository.getRemoteMovieList(API_KEY, page)
            if (!movies.isNullOrEmpty()) {
                for (movie in movies) {
                    setMovieGenres(movie)
                    saveLikeStatus(movie)
                }
                if (page == 1) {
                    movieRepository.deleteLocalMovies()
                    movieRepository.insertLocalMovies(movies)
                }
            }
            movies
        } catch (e: Exception) {
            movieRepository.getLocalMovies()
        }
    }

    private suspend fun getFavourites(): List<Movie>? {
        return try {
            val movies = movieRepository.getRemoteFavouriteMovies(API_KEY, sessionId)

            if (!movies.isNullOrEmpty()) {
                for (movie in movies) {
                    setMovieGenres(movie)
                    movie.isClicked = true
                }
            }
            movies
        } catch (e: Exception) {
            movieRepository.getLocalFavouriteMovies()
        }
    }

    private fun setMovieGenres(movie: Movie) {
        movie.genreNames = ""
        movie.genreIds?.forEach { genreId ->
            val genreName = GenresList.genres?.get(genreId)
                .toString().toLowerCase(Locale.ROOT)
            movie.genreNames += context.getString(R.string.genre_name, genreName)
        }
        movie.genreNames = movie.genreNames.substring(0, movie.genreNames.length - 2)
    }

    fun addToFavourites(item: Movie) {
        item.isClicked = !item.isClicked
        val selectedMovie = SelectedMovie(MEDIA_TYPE, item.id, item.isClicked)
        updateFavourites(selectedMovie)
    }

    private fun updateFavourites(movie: SelectedMovie) {
        launch {
            try {
                movieRepository.addRemoveRemoteFavourites(API_KEY, sessionId, movie)
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    movieRepository.updateLocalMovieIsCLicked(movie.selectedStatus, movie.movieId)
                    movieRepository.insertLocalMovieStatus(
                        MovieStatus(movie.movieId, movie.selectedStatus)
                    )
                }
            }
        }
    }

    private fun saveLikeStatus(movie: Movie) {
        launch {
            try {
                val movieStatus = movieRepository.getRemoteMovieStates(
                    movie.id,
                    API_KEY, sessionId
                )
                if (movieStatus != null) {
                    movie.isClicked = movieStatus
                    withContext(Dispatchers.IO) {
                        movieRepository.updateLocalMovieIsCLicked(movie.isClicked, movie.id)
                    }
                    liveData.value = State.Update
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

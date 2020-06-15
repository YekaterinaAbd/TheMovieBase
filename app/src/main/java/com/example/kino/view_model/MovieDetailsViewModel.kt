package com.example.kino.view_model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.example.kino.R
import com.example.kino.model.movie.Movie
import com.example.kino.model.movie.MovieStatus
import com.example.kino.model.movie.SelectedMovie
import com.example.kino.model.repository.MovieRepository
import com.example.kino.utils.constants.API_KEY
import com.example.kino.utils.constants.MEDIA_TYPE
import com.example.kino.utils.constants.NULLABLE_VALUE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MovieDetailsViewModel(
    private val context: Context,
    private val movieRepository: MovieRepository
) : BaseViewModel() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var sessionId: String

    val liveData = MutableLiveData<State>()

    init {
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

    fun getMovie(id: Int) {

        launch {
            val movie = withContext(Dispatchers.IO) {
                try {
                    val movieDetails = movieRepository.getRemoteMovie(
                        id,
                        API_KEY
                    )
                    val movieState = movieRepository.getRemoteMovieStates(
                        id,
                        API_KEY, sessionId
                    )
                    if (movieDetails != null) {
                        if (movieState != null) {
                            movieDetails.isClicked = movieState
                        }
                        setGenres(movieDetails)
                        movieDetails.tagline?.let {
                            movieDetails.runtime?.let { it1 ->
                                movieRepository.updateLocalMovieProperties(
                                    it,
                                    it1, movieDetails.id
                                )
                            }
                        }
                    }
                    return@withContext movieDetails

                } catch (e: Exception) {
                    movieRepository.getLocalMovie(id)
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(movie)
        }
    }

    fun updateLikeStatus(movie: Movie) {

        launch {
            withContext(Dispatchers.IO) {
                val selectedMovie = SelectedMovie(MEDIA_TYPE, movie.id, movie.isClicked)
                try {
                    movieRepository.updateRemoteFavourites(API_KEY, sessionId, selectedMovie)
                    movieRepository.updateLocalMovieIsCLicked(movie.isClicked, movie.id)
                } catch (e: Exception) {
                    movieRepository.updateLocalMovieIsCLicked(movie.isClicked, movie.id)
                    movieRepository.insertLocalMovieStatus(MovieStatus(movie.id, movie.isClicked))
                }
            }
        }
    }

    private fun setGenres(movie: Movie) {
        movie.genreNames = ""
        if (movie.genres != null) {
            for (i in movie.genres.indices) {
                if (i == 0) movie.genreNames += movie.genres[i].genre.toLowerCase(Locale.ROOT)
                else movie.genreNames += ", " + movie.genres[i].genre.toLowerCase(Locale.ROOT)
            }
        } else {
            movie.genreNames = movie.genreNames.substring(0, movie.genreNames.length)
        }
    }

    sealed class State {
        object HideLoading : State()
        data class Result(val movie: Movie?) : State()
    }
}

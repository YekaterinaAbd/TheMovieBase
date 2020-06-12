package com.example.kino.view_model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.example.kino.R
import com.example.kino.model.movie.Movie
import com.example.kino.model.movie.MovieStatus
import com.example.kino.model.movie.SelectedMovie
import com.example.kino.model.repository.MovieRepository
import com.example.kino.utils.API_KEY
import com.example.kino.utils.MEDIA_TYPE
import com.example.kino.utils.NULLABLE_VALUE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailsViewModel(
    private val context: Context,
    private val movieRepository: MovieRepository
) : BaseViewModel() {

    val liveData = MutableLiveData<State>()

    private lateinit var sharedPref: SharedPreferences
    private lateinit var sessionId: String

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
                    val movieDetails = movieRepository.getRemoteMovie(id, API_KEY)
                    val movieState = movieRepository.getRemoteMovieStates(id, API_KEY, sessionId)
                    if (movieDetails != null) {
                        if (movieState != null) {
                            movieDetails.isClicked = movieState
                        }
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

    fun updateLike(movie: Movie) {
        launch {
            val selectedMovie = SelectedMovie(MEDIA_TYPE, movie.id, movie.isClicked)
            try {
                movieRepository.addRemoveRemoteFavourites(API_KEY, sessionId, selectedMovie)
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    movieRepository.updateLocalMovieIsCLicked(movie.isClicked, movie.id)
                    movieRepository.insertLocalMovieStatus(MovieStatus(movie.id, movie.isClicked))
                }
            }
        }
    }

    sealed class State {
        object HideLoading : State()
        data class Result(val movie: Movie?) : State()
    }
}
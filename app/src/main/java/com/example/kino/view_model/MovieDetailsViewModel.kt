package com.example.kino.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.kino.model.database.MovieDao
import com.example.kino.model.database.MovieDatabase
import com.example.kino.model.movie.Movie
import com.example.kino.model.repository.MovieRepositoryImpl
import com.example.kino.utils.API_KEY
import com.example.kino.utils.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailsViewModel(context: Context) : BaseViewModel() {

    private var movieDao: MovieDao = MovieDatabase.getDatabase(context = context).movieDao()
    private var movieRepository = MovieRepositoryImpl(movieDao, RetrofitService.getPostApi())
    val liveData = MutableLiveData<State>()

    fun getMovie(id: Int) {

        launch {
            val movie = withContext(Dispatchers.IO) {
                try {
                    val movieDetails = movieRepository.getRemoteMovie(id, API_KEY)
                    if (movieDetails != null) {
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

    sealed class State {
        object HideLoading : State()
        data class Result(val movie: Movie?) : State()
    }
}
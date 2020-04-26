package com.example.kino.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.utils.RetrofitService
import com.example.kino.model.database.MovieDao
import com.example.kino.model.database.MovieDatabase
import com.example.kino.model.movie.Movie
import com.example.kino.utils.Constants
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MovieDetailsViewModel(context: Context) : ViewModel(), CoroutineScope {

    private var movieDao: MovieDao = MovieDatabase.getDatabase(context = context).movieDao()
    private val constants: Constants = Constants()
    val liveData = MutableLiveData<State>()

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getMovie(id: Int) {

        launch {
            val movie = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getPostApi().getMovieById(id, constants.apiKey)
                    if (response.isSuccessful) {
                        val movieDetails = response.body()
                        if (movieDetails != null) {
                            movieDetails.runtime?.let { movieDao.updateMovieRuntime(it, id) }
                            movieDetails.tagline?.let { movieDao.updateMovieTagline(it, id) }
                        }
                        return@withContext movieDetails
                    } else {
                        return@withContext movieDao.getMovie(id)
                    }

                } catch (e: Exception) {
                    movieDao.getMovie(id)
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
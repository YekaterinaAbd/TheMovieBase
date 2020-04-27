package com.example.kino.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.kino.model.database.MovieDao
import com.example.kino.model.database.MovieDatabase
import com.example.kino.model.movie.Movie
import com.example.kino.utils.RetrofitService
import com.example.kino.utils.API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailsViewModel(context: Context) : BaseViewModel() {

    private var movieDao: MovieDao = MovieDatabase.getDatabase(context = context).movieDao()
    val liveData = MutableLiveData<State>()

    fun getMovie(id: Int) {

        launch {
            val movie = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getPostApi().getMovieById(id, API_KEY)
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
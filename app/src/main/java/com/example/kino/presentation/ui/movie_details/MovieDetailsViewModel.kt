package com.example.kino.presentation.ui.movie_details

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.kino.data.model.movie.FavouriteMovie
import com.example.kino.data.model.movie.KeyWord
import com.example.kino.data.model.movie.RemoteMovieDetails
import com.example.kino.data.model.movie.Video
import com.example.kino.data.network.API_KEY
import com.example.kino.domain.model.Movie
import com.example.kino.domain.repository.MovieRepository
import com.example.kino.presentation.BaseViewModel
import com.example.kino.presentation.utils.constants.MEDIA_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailsViewModel(
    context: Context,
    private val movieRepository: MovieRepository
) : BaseViewModel() {

    private var sessionId = movieRepository.getLocalSessionId(context)
    val liveData = MutableLiveData<State>()

    fun getMovie(id: Int) {

        launch {
            val movie = withContext(Dispatchers.IO) {
                try {
                    val movieDetails = movieRepository.getRemoteMovie(id, API_KEY)
                    val movieState = movieRepository.getRemoteMovieStates(
                        id,
                        API_KEY, sessionId
                    )
                    if (movieDetails != null) {
                        if (movieState != null) {
                            movieDetails.isClicked = movieState
                        }
                        // setGenres(movieDetails)
//                        movieDetails.tagLine?.let {
//                            movieDetails.runtime?.let { it1 ->
//                                movieDetails.id?.let { it2 ->
//                                    movieRepository.updateLocalMovieProperties(it, it1, it2)
//                                }
//                            }
//                        }
                    }
                    return@withContext movieDetails

                } catch (e: Exception) {
                    RemoteMovieDetails()
                    //movieRepository.getLocalMovie(id)
                }
            }
            liveData.value = State.HideLoading
            if (movie?.id == null) liveData.value = State.Error
            else liveData.value = State.Result(movie)
        }
    }

    fun getSimilarMovies(id: Int) {
        launch {
            val movies = movieRepository.getSimilarMovies(id, API_KEY)
            liveData.value = State.SimilarMoviesResult(movies)
        }
    }

    fun getKeyWords(id: Int) {
        launch {
            val keyWordsList = movieRepository.getKeywords(id, API_KEY)
            liveData.value = State.KeyWordsListResult(keyWordsList)
        }
    }

    fun getTrailer(id: Int) {
        launch {
            val trailer = withContext(Dispatchers.IO) {
                try {
                    movieRepository.getTrailer(id, API_KEY)
                } catch (e: Exception) {
                    Video()
                }
            }
            liveData.value = State.TrailerResult(trailer)
        }
    }

    fun updateLikeStatus(id: Int, isClicked: Boolean) {
        val movie = FavouriteMovie(MEDIA_TYPE, id, isClicked)
        launch {
            movieRepository.updateLikeStatus(movie, sessionId)
        }
    }

//    private fun setGenres(movie: RemoteMovieDetails) {
//        movie.genreNames = ""
//        for (i in movie.genres.indices) {
//            if (i == 0) movie.genreNames += movie.genres[i].genre.toLowerCase(Locale.ROOT)
//            else movie.genreNames += ", " + movie.genres[i].genre.toLowerCase(Locale.ROOT)
//        }
//    }

    sealed class State {
        object HideLoading : State()
        object Error : State()
        data class Result(val movie: RemoteMovieDetails?) : State()
        data class TrailerResult(val trailer: Video?) : State()
        data class SimilarMoviesResult(val movies: List<Movie>?) : State()
        data class KeyWordsListResult(val keywords: List<KeyWord>?) : State()
    }
}

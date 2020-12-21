package com.example.movies.presentation.ui.movie_details

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.movies.data.model.movie.*
import com.example.movies.domain.model.Movie
import com.example.movies.domain.use_case.LikesUseCase
import com.example.movies.domain.use_case.MovieDetailsUseCase
import com.example.movies.domain.use_case.SessionIdUseCase
import com.example.movies.presentation.BaseViewModel
import com.example.movies.presentation.utils.constants.MEDIA_TYPE
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    context: Context,
    sessionIdUseCase: SessionIdUseCase,
    private val movieDetailsUseCase: MovieDetailsUseCase,
    private val likesUseCase: LikesUseCase
) : BaseViewModel() {

    private var sessionId = sessionIdUseCase.getLocalSessionId(context)
    val liveData = MutableLiveData<State>()

    fun getMovie(id: Int) {
        uiScope.launch {
            val movieDetails: MovieDetails? = movieDetailsUseCase.getMovie(id)
            val movieState = likesUseCase.getMovieStates(
                id, sessionId
            )
            if (movieDetails != null) {
                if (movieState != null) {
                    movieDetails.favourite = movieState.favourite
                    movieDetails.watchlist = movieState.watchlist
                    if (movieState.rated != false) {
                        val rating = movieState.rated as LinkedTreeMap<String, Double>
                        movieDetails.userRating = rating["value"]
                    }
                }
                liveData.value = State.Result(movieDetails)
            } else liveData.value = State.Error
            liveData.value = State.HideLoading
        }
    }

    fun getSimilarMovies(id: Int) {
        uiScope.launch {
            val movies = movieDetailsUseCase.getSimilarMovies(id)
            liveData.value = State.SimilarMoviesResult(movies)
        }
    }

    fun getKeyWords(id: Int) {
        uiScope.launch {
            val keyWordsList = movieDetailsUseCase.getKeywords(id)
            liveData.value = State.KeyWordsListResult(keyWordsList)
        }
    }

    fun getTrailer(id: Int) {
        uiScope.launch {
            val trailer = movieDetailsUseCase.getTrailer(id)
            liveData.value = State.TrailerResult(trailer)
        }
    }

    fun getCredits(id: Int) {
        uiScope.launch {
            val credits = movieDetailsUseCase.getCredits(id)
            liveData.value = State.CreditsResult(credits)
        }
    }

    fun updateFavouriteStatus(id: Int, isClicked: Boolean) {
        val movie = FavouriteMovie(MEDIA_TYPE, id, isClicked)
        uiScope.launch {
            likesUseCase.updateIsFavourite(movie, sessionId)
        }
    }

    fun updateWatchListStatus(id: Int, isClicked: Boolean) {
        val movie = WatchListMovie(MEDIA_TYPE, id, isClicked)
        uiScope.launch {
            likesUseCase.updateIsInWatchList(movie, sessionId)
        }
    }

    fun rateMovie(id: Int, rating: Double) {
        uiScope.launch {
            val result = movieDetailsUseCase.rateMovie(id, sessionId, rating)
            liveData.value = State.RatingResult(result)
        }
    }

    sealed class State {
        object HideLoading : State()
        object Error : State()
        data class Result(val movie: MovieDetails?) : State()
        data class TrailerResult(val trailer: Video?) : State()
        data class SimilarMoviesResult(val movies: List<Movie>?) : State()
        data class KeyWordsListResult(val keywords: List<KeyWord>?) : State()
        data class CreditsResult(val credits: Credits?) : State()
        data class RatingResult(val success: Boolean) : State()
    }
}

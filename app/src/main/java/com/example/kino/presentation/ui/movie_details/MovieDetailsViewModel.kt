package com.example.kino.presentation.ui.movie_details

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.kino.data.model.movie.*
import com.example.kino.domain.model.Movie
import com.example.kino.domain.use_case.LikesUseCase
import com.example.kino.domain.use_case.MovieDetailsUseCase
import com.example.kino.domain.use_case.SessionIdUseCase
import com.example.kino.presentation.BaseViewModel
import com.example.kino.presentation.utils.constants.MEDIA_TYPE
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    context: Context,
    private val movieDetailsUseCase: MovieDetailsUseCase,
    sessionIdUseCase: SessionIdUseCase,
    private val likesUseCase: LikesUseCase
) : BaseViewModel() {

    private var sessionId = sessionIdUseCase.getLocalSessionId(context)
    val liveData = MutableLiveData<State>()

    fun getMovie(id: Int) {
        uiScope.launch {
            val movieDetails = movieDetailsUseCase.getMovie(id)
            val movieState = likesUseCase.getRemoteMovieStatuses(
                id, sessionId
            )
            if (movieDetails != null) {
                if (movieState != null) {
                    movieDetails.favourite = movieState.favourite
                    movieDetails.watchlist = movieState.watchlist
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

    sealed class State {
        object HideLoading : State()
        object Error : State()
        data class Result(val movie: RemoteMovieDetails?) : State()
        data class TrailerResult(val trailer: Video?) : State()
        data class SimilarMoviesResult(val movies: List<Movie>?) : State()
        data class KeyWordsListResult(val keywords: List<KeyWord>?) : State()
    }
}

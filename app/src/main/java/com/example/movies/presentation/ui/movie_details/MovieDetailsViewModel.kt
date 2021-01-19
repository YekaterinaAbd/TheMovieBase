package com.example.movies.presentation.ui.movie_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movies.data.model.movie.FavouriteMovie
import com.example.movies.data.model.movie.MovieDetails
import com.example.movies.data.model.movie.WatchListMovie
import com.example.movies.domain.use_case.LikesUseCase
import com.example.movies.domain.use_case.MovieDetailsUseCase
import com.example.movies.domain.use_case.SessionIdUseCase
import com.example.movies.presentation.ui.MovieDetailsState
import com.example.movies.presentation.utils.constants.MEDIA_TYPE
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    sessionIdUseCase: SessionIdUseCase,
    private val movieDetailsUseCase: MovieDetailsUseCase,
    private val likesUseCase: LikesUseCase
) : ViewModel() {

    private var sessionId = sessionIdUseCase.getLocalSessionId()
    val liveData = MutableLiveData<MovieDetailsState>()

    fun getMovie(id: Int) {
        viewModelScope.launch {
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
                liveData.value = MovieDetailsState.Result(movieDetails)
            } else liveData.value = MovieDetailsState.Error
            liveData.value = MovieDetailsState.HideLoading
        }
    }

    fun getSimilarMovies(id: Int) {
        viewModelScope.launch {
            val movies = movieDetailsUseCase.getSimilarMovies(id)
            liveData.value = MovieDetailsState.SimilarMoviesResult(movies)
        }
    }

    fun getKeyWords(id: Int) {
        viewModelScope.launch {
            val keyWordsList = movieDetailsUseCase.getKeywords(id)
            liveData.value = MovieDetailsState.KeyWordsListResult(keyWordsList)
        }
    }

    fun getTrailer(id: Int) {
        viewModelScope.launch {
            val trailer = movieDetailsUseCase.getTrailer(id)
            liveData.value = MovieDetailsState.TrailerResult(trailer)
        }
    }

    fun getCredits(id: Int) {
        viewModelScope.launch {
            val credits = movieDetailsUseCase.getCredits(id)
            liveData.value = MovieDetailsState.CreditsResult(credits)
        }
    }

    fun updateFavouriteStatus(id: Int, isClicked: Boolean) {
        val movie = FavouriteMovie(MEDIA_TYPE, id, isClicked)
        viewModelScope.launch {
            likesUseCase.updateIsFavourite(movie, sessionId)
        }
    }

    fun updateWatchListStatus(id: Int, isClicked: Boolean) {
        val movie = WatchListMovie(MEDIA_TYPE, id, isClicked)
        viewModelScope.launch {
            likesUseCase.updateIsInWatchList(movie, sessionId)
        }
    }

    fun rateMovie(id: Int, rating: Double) {
        viewModelScope.launch {
            val result = movieDetailsUseCase.rateMovie(id, sessionId, rating)
            liveData.value = MovieDetailsState.RatingResult(result)
        }
    }
}

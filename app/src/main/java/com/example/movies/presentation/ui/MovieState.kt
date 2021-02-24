package com.example.movies.presentation.ui

import com.example.movies.data.model.account.Account
import com.example.movies.data.model.movie.Credits
import com.example.movies.data.model.movie.Keyword
import com.example.movies.data.model.movie.MovieDetails
import com.example.movies.data.model.movie.Video
import com.example.movies.domain.model.DataSource
import com.example.movies.domain.model.Movie
import com.example.movies.domain.model.MoviesType

sealed class LoadingState {
    object ShowLoading : LoadingState()
    object HideLoading : LoadingState()
    object ShowPageLoading : LoadingState()
    object HidePageLoading : LoadingState()
    data class Error(val error: String?) : LoadingState()
}

sealed class AccountState {
    data class AccountResult(val data: Account) : AccountState()
    data class AccountLocalResult(val username: String) : AccountState()
    object LogOutSuccessful : AccountState()
    object LogOutFailed : AccountState()
}

sealed class SignInState {
    object ShowLoading : SignInState()
    object HideLoading : SignInState()
    object FailedLoading : SignInState()
    object WrongDataProvided : SignInState()
    object Result : SignInState()
}

sealed class MoviesState {
    object ShowLoading : MoviesState()
    object HideLoading : MoviesState()
    data class MovieStates(val favourite: Boolean, val watchlist: Boolean, val rating: Double) :
        MoviesState()

    data class Result(
        val moviesList: List<Movie>,
        val dataSource: DataSource? = null,
        val type: MoviesType? = null,
        val totalPages: Int = 1
    ) : MoviesState()
}

sealed class MovieDetailsState {
    object HideLoading : MovieDetailsState()
    object Error : MovieDetailsState()
    data class Result(val movie: MovieDetails?) : MovieDetailsState()
    data class TrailerResult(val trailer: Video?) : MovieDetailsState()
    data class SimilarMoviesResult(val movies: List<Movie>?) : MovieDetailsState()
    data class KeyWordsListResult(val keywords: List<Keyword>?) : MovieDetailsState()
    data class CreditsResult(val credits: Credits?) : MovieDetailsState()
    data class RatingResult(val success: Boolean) : MovieDetailsState()
}



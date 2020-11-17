package com.example.kino.presentation.ui

sealed class MovieState {
    object ShowLoading : MovieState()
    object HideLoading : MovieState()
    object ShowPageLoading : MovieState()
    object HidePageLoading : MovieState()
    data class Error(val error: String?) : MovieState()
}

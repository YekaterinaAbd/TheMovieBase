package com.example.kino.presentation.module

import com.example.kino.presentation.ThemeViewModel
import com.example.kino.presentation.ui.account.AccountViewModel
import com.example.kino.presentation.ui.lists.MoviesListViewModel
import com.example.kino.presentation.ui.markers.MarkersViewModel
import com.example.kino.presentation.ui.movie_details.MovieDetailsViewModel
import com.example.kino.presentation.ui.sign_in.SignInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MoviesListViewModel(
            context = get(),
            likes = get(),
            localMovies = get(),
            localSessionId = get(),
            moviesLists = get()
        )
    }
    viewModel { MovieDetailsViewModel(context = get(), movieRepository = get()) }
    viewModel { AccountViewModel(context = get(), accountRepository = get()) }
    viewModel { MarkersViewModel(markerRepository = get()) }
    viewModel { SignInViewModel(context = get(), accountRepository = get()) }
    viewModel { ThemeViewModel(context = get(), accountRepository = get()) }
}

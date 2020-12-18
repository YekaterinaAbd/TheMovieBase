package com.example.movies.presentation.module

import com.example.movies.presentation.ThemeViewModel
import com.example.movies.presentation.ui.account.AccountViewModel
import com.example.movies.presentation.ui.lists.MoviesListViewModel
import com.example.movies.presentation.ui.lists.search.SearchViewModel
import com.example.movies.presentation.ui.markers.MarkersViewModel
import com.example.movies.presentation.ui.movie_details.MovieDetailsViewModel
import com.example.movies.presentation.ui.sign_in.SignInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MoviesListViewModel(
            context = get(),
            likesUseCase = get(),
            localMoviesUseCase = get(),
            sessionIdUseCase = get(),
            moviesListsUseCase = get(),
            searchMoviesUseCase = get()
        )
    }
    viewModel { SearchViewModel(context = get(), searchUseCase = get()) }
    viewModel {
        MovieDetailsViewModel(
            context = get(),
            movieDetailsUseCase = get(),
            sessionIdUseCase = get(),
            likesUseCase = get()
        )
    }
    viewModel { AccountViewModel(context = get(), accountUseCase = get()) }
    viewModel { MarkersViewModel(markerRepository = get()) }
    viewModel {
        SignInViewModel(
            context = get(),
            loginUseCase = get(),
            localLoginDataUseCase = get()
        )
    }
    viewModel { ThemeViewModel(context = get(), accountRepository = get()) }
}

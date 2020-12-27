package com.example.movies.presentation.di

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
    viewModel {
        MovieDetailsViewModel(
            movieDetailsUseCase = get(),
            sessionIdUseCase = get(),
            likesUseCase = get()
        )
    }
    viewModel { SearchViewModel(searchUseCase = get(), recentMoviesUseCase = get()) }
    viewModel { AccountViewModel(accountUseCase = get()) }
    viewModel { MarkersViewModel(markerRepository = get()) }
    viewModel { SignInViewModel(loginUseCase = get(), localLoginDataUseCase = get()) }
    viewModel { ThemeViewModel(accountUseCase = get()) }
}

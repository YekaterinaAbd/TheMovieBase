package com.example.movies.data.di

import com.example.movies.data.repository.AccountRepositoryImpl
import com.example.movies.data.repository.MarkerRepositoryImpl
import com.example.movies.data.repository.MovieRepositoryImpl
import com.example.movies.data.repository.SearchRepositoryImpl
import com.example.movies.domain.repository.AccountRepository
import com.example.movies.domain.repository.MarkerRepository
import com.example.movies.domain.repository.MovieRepository
import com.example.movies.domain.repository.SearchRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<MovieRepository> {
        MovieRepositoryImpl(
            api = get(),
            movieDao = get(),
            movieStatusDao = get(),
            recentMovieDao = get(),
            remoteMovieMapper = get(),
            localMovieMapper = get()
        )
    }
    single<AccountRepository> {
        AccountRepositoryImpl(
            api = get(),
            context = get(),
            sharedPreferences = get()
        )
    }
    single<MarkerRepository> { MarkerRepositoryImpl(markerDao = get()) }
    single<SearchRepository> {
        SearchRepositoryImpl(
            searchHistoryDao = get(),
            recentMovieDao = get()
        )
    }
}
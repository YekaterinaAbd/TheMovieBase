package com.example.kino.data.module

import com.example.kino.data.repository.AccountRepositoryImpl
import com.example.kino.data.repository.MarkerRepositoryImpl
import com.example.kino.data.repository.MovieRepositoryImpl
import com.example.kino.domain.repository.AccountRepository
import com.example.kino.domain.repository.MarkerRepository
import com.example.kino.domain.repository.MovieRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<MovieRepository> {
        MovieRepositoryImpl(
            movieDao = get(),
            service = get(),
            movieStatusDao = get(),
            sharedPreferences = get(),
            remoteMovieMapper = get(),
            localMovieMapper = get()
        )
    }
    single<AccountRepository> { AccountRepositoryImpl(service = get(), sharedPreferences = get()) }
    single<MarkerRepository> { MarkerRepositoryImpl(markerDao = get()) }
}
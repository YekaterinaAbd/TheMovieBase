package com.example.movies.domain.di

import com.example.movies.domain.use_case.*
import org.koin.dsl.module

val useCaseModule = module {
    factory { SessionIdUseCase(accountRepository = get()) }
    factory { LikesUseCase(movieRepository = get()) }
    factory { MoviesListsUseCase(movieRepository = get()) }
    factory { LocalMoviesUseCase(movieRepository = get()) }
    factory { SearchUseCase(searchRepository = get()) }
    factory { SearchMoviesUseCase(movieRepository = get()) }
    factory { AccountUseCase(accountRepository = get()) }
    factory { MovieDetailsUseCase(movieRepository = get()) }
    factory { LoginUseCase(accountRepository = get()) }
    factory { LocalLoginDataUseCase(accountRepository = get()) }
    factory { RecentMoviesUseCase(searchRepository = get()) }
}

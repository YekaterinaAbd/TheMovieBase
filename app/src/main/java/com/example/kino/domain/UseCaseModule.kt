package com.example.kino.domain

import com.example.kino.domain.use_case.LikesUseCase
import com.example.kino.domain.use_case.LocalMoviesUseCase
import com.example.kino.domain.use_case.MoviesListsUseCase
import com.example.kino.domain.use_case.SessionIdUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { SessionIdUseCase(movieRepository = get()) }
    factory { LikesUseCase(movieRepository = get()) }
    factory { MoviesListsUseCase(movieRepository = get()) }
    factory { LocalMoviesUseCase(movieRepository = get()) }
}
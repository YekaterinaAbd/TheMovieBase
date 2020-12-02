package com.example.kino.domain.module

import com.example.kino.domain.use_case.*
import org.koin.dsl.module

val useCaseModule = module {
    factory { SessionIdUseCase(movieRepository = get()) }
    factory { LikesUseCase(movieRepository = get()) }
    factory { MoviesListsUseCase(movieRepository = get()) }
    factory { LocalMoviesUseCase(movieRepository = get()) }
    factory { SearchUseCase(searchRepository = get()) }
    factory { SearchMoviesUseCase(movieRepository = get()) }
    factory { AccountUseCase(accountRepository = get()) }
    factory { MovieDetailsUseCase(movieRepository = get()) }
    factory { LoginUseCase(accountRepository = get()) }
    factory { LocalLoginDataUseCase(accountRepository = get()) }
}

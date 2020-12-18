package com.example.movies.data.module

import com.example.movies.data.mapper.LocalMovieMapper
import com.example.movies.data.mapper.RemoteMovieMapper
import org.koin.dsl.module

val mapperModule = module {
    factory { LocalMovieMapper() }
    factory { RemoteMovieMapper() }
}
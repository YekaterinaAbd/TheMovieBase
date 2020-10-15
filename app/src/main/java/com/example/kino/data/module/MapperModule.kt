package com.example.kino.data.module

import com.example.kino.data.mapper.LocalMovieMapper
import com.example.kino.data.mapper.RemoteMovieMapper
import org.koin.dsl.module

val mapperModule = module {
    factory { LocalMovieMapper() }
    factory { RemoteMovieMapper() }
}
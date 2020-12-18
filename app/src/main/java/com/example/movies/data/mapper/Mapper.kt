package com.example.movies.data.mapper

interface Mapper<N, M> {
    fun from(model: N): M
    fun to(model: M): N
}

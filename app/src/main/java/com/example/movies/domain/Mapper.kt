package com.example.movies.domain

interface Mapper<N, M> {
    fun from(model: N): M
    fun to(model: M): N
}

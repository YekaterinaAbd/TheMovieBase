package com.example.kino.domain.use_case

import com.example.kino.data.network.API_KEY
import com.example.kino.domain.repository.MovieRepository

class MovieDetailsUseCase(private val movieRepository: MovieRepository) {
    suspend fun getMovie(id: Int) = movieRepository.getRemoteMovie(id, API_KEY)
    suspend fun getSimilarMovies(id: Int) = movieRepository.getSimilarMovies(id, API_KEY)
    suspend fun getKeywords(id: Int) = movieRepository.getKeywords(id, API_KEY)
    suspend fun getTrailer(id: Int) = movieRepository.getTrailer(id, API_KEY)
}
package com.example.movies.domain.use_case

import com.example.movies.data.network.API_KEY
import com.example.movies.domain.repository.MovieRepository

class MovieDetailsUseCase(private val movieRepository: MovieRepository) {
    suspend fun getMovie(id: Int) = movieRepository.getRemoteMovie(id, API_KEY)
    suspend fun getSimilarMovies(id: Int) = movieRepository.getSimilarMovies(id, API_KEY)
    suspend fun getKeywords(id: Int) = movieRepository.getKeywords(id, API_KEY)
    suspend fun getTrailer(id: Int) = movieRepository.getTrailer(id, API_KEY)
    suspend fun getCredits(id: Int) = movieRepository.getCredits(id, API_KEY)
    suspend fun rateMovie(id: Int, sessionId: String, rating: Double) =
        movieRepository.rateMovie(id, API_KEY, sessionId, rating)
}
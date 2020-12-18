package com.example.movies.domain.use_case

import android.content.Context
import com.example.movies.domain.repository.MovieRepository

class SessionIdUseCase(
    private val movieRepository: MovieRepository
) {
    fun getLocalSessionId(context: Context) = movieRepository.getLocalSessionId(context)
}

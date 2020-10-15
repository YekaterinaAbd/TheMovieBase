package com.example.kino.domain.use_case

import android.content.Context
import com.example.kino.domain.repository.MovieRepository

class SessionIdUseCase(
    private val movieRepository: MovieRepository
) {
    fun getLocalSessionId(context: Context) = movieRepository.getLocalSessionId(context)
}
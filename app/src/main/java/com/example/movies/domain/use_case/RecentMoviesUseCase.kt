package com.example.movies.domain.use_case

import com.example.movies.data.model.entities.RecentMovie
import com.example.movies.domain.repository.SearchRepository

class RecentMoviesUseCase(
    private val searchRepository: SearchRepository
) {
    suspend fun getRecentMovies(): List<RecentMovie> = searchRepository.getRecentMovies()
    suspend fun deleteRecentMovies() = searchRepository.deleteRecentMovies()
}

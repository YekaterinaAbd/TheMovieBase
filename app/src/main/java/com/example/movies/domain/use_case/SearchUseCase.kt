package com.example.movies.domain.use_case

import com.example.movies.data.model.entities.SearchQuery
import com.example.movies.domain.repository.SearchRepository

class SearchUseCase(
    private val searchRepository: SearchRepository
) {
    suspend fun getAllQueries() = searchRepository.getAllQueries()

    suspend fun getLastQueries() = searchRepository.getLastQueries()

    suspend fun insertQuery(query: SearchQuery) = searchRepository.insertQuery(query)

    suspend fun deleteQuery(id: Int) = searchRepository.deleteQuery(id)

    suspend fun deleteAllQueries() = searchRepository.deleteAllQueries()
}

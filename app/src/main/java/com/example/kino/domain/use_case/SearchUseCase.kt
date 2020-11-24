package com.example.kino.domain.use_case

import com.example.kino.data.model.entities.SearchQuery
import com.example.kino.domain.repository.SearchRepository

class SearchUseCase(
    private val searchRepository: SearchRepository
) {
    suspend fun getAllQueries() = searchRepository.getAllQueries()

    suspend fun getLastQueries() = searchRepository.getLastQueries()

    suspend fun insertQuery(query: SearchQuery) = searchRepository.insertQuery(query)

    suspend fun deleteQuery(id: Int) = searchRepository.deleteQuery(id)

    suspend fun deleteAllQueries() = searchRepository.deleteAllQueries()
}

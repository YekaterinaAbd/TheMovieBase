package com.example.kino.domain.repository

import com.example.kino.data.model.entities.SearchQuery

interface SearchRepository {
    suspend fun getAllQueries(): List<SearchQuery>?
    suspend fun getLastQueries(): List<SearchQuery>?
    suspend fun insertQuery(query: SearchQuery): String?
    suspend fun deleteQuery(id: Int): String?
    suspend fun deleteAllQueries(): String?
}

package com.example.movies.domain.repository

import com.example.movies.data.model.entities.RecentMovie
import com.example.movies.data.model.entities.SearchQuery

interface SearchRepository {
    suspend fun getAllQueries(): List<SearchQuery>?
    suspend fun getLastQueries(): List<SearchQuery>?
    suspend fun insertQuery(query: SearchQuery): String?
    suspend fun deleteQuery(id: Int): String?
    suspend fun deleteAllQueries(): String?
    suspend fun getRecentMovies(): List<RecentMovie>
    suspend fun deleteRecentMovies()
}

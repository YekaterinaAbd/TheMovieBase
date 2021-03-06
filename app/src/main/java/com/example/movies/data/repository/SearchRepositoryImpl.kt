package com.example.movies.data.repository

import com.example.movies.data.database.RecentMovieDao
import com.example.movies.data.database.SearchHistoryDao
import com.example.movies.data.model.entities.RecentMovie
import com.example.movies.data.model.entities.SearchQuery
import com.example.movies.domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchRepositoryImpl(
    private val searchHistoryDao: SearchHistoryDao,
    private val recentMovieDao: RecentMovieDao
) : SearchRepository {

    override suspend fun getAllQueries(): List<SearchQuery> = withContext(Dispatchers.IO) {
        try {
            return@withContext searchHistoryDao.getAll()
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }

    override suspend fun getLastQueries(): List<SearchQuery> = withContext(Dispatchers.IO) {
        try {
            return@withContext searchHistoryDao.getQueries()
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }

    override suspend fun insertQuery(query: SearchQuery): String? = withContext(Dispatchers.IO) {
        try {
            if (searchHistoryDao.getQueriesCount() >= 10) searchHistoryDao.deleteFirst()
            searchHistoryDao.insertQuery(query)
            return@withContext null

        } catch (e: Exception) {
            return@withContext e.message
        }
    }

    override suspend fun deleteQuery(id: Int) = withContext(Dispatchers.IO) {
        try {
            searchHistoryDao.deleteQuery(id)
            return@withContext null
        } catch (e: Exception) {
            return@withContext e.message
        }
    }

    override suspend fun deleteAllQueries() = withContext(Dispatchers.IO) {
        try {
            searchHistoryDao.deleteAll()
            return@withContext null
        } catch (e: Exception) {
            return@withContext e.message
        }
    }

    override suspend fun getRecentMovies(): List<RecentMovie> = withContext(Dispatchers.IO) {
        try {
            return@withContext recentMovieDao.getRecentMovies() ?: emptyList()
        } catch (e: java.lang.Exception) {
            return@withContext emptyList()
        }
    }

    override suspend fun deleteRecentMovies() {
        withContext(Dispatchers.IO) {
            recentMovieDao.deleteAll()
        }
    }
}
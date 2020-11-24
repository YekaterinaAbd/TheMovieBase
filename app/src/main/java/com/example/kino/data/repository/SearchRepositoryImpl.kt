package com.example.kino.data.repository

import android.util.Log
import com.example.kino.data.database.SearchHistoryDao
import com.example.kino.data.model.entities.SearchQuery
import com.example.kino.domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val KEY = "search_repository"

class SearchRepositoryImpl(
    private val searchHistoryDao: SearchHistoryDao
) : SearchRepository {

    override suspend fun getAllQueries(): List<SearchQuery>? = withContext(Dispatchers.IO) {
        try {
            return@withContext searchHistoryDao.getAll()
        } catch (e: Exception) {
            Log.d(KEY, e.message.toString())
            return@withContext emptyList()
        }
    }

    override suspend fun getLastQueries(): List<SearchQuery>? = withContext(Dispatchers.IO) {
        try {
            return@withContext searchHistoryDao.getQueries()
        } catch (e: Exception) {
            Log.d(KEY, e.message.toString())
            return@withContext emptyList()
        }
    }

    override suspend fun insertQuery(query: SearchQuery): String? = withContext(Dispatchers.IO) {
        try {
            searchHistoryDao.insertQuery(query)
            return@withContext null

        } catch (e: Exception) {
            Log.d(KEY, e.message.toString())
            return@withContext e.message
        }
    }

    override suspend fun deleteQuery(id: Int) = withContext(Dispatchers.IO) {
        try {
            searchHistoryDao.deleteQuery(id)
            return@withContext null
        } catch (e: Exception) {
            Log.d(KEY, e.message.toString())
            return@withContext e.message
        }
    }

    override suspend fun deleteAllQueries() = withContext(Dispatchers.IO) {
        try {
            searchHistoryDao.deleteAll()
            return@withContext null
        } catch (e: Exception) {
            Log.d(KEY, e.message.toString())
            return@withContext e.message
        }
    }
}
package com.example.movies.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movies.data.model.entities.SearchQuery

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuery(query: SearchQuery)

    @Query("SELECT * FROM search_history")
    fun getAll(): List<SearchQuery>

    @Query("SELECT * FROM search_history ORDER BY id DESC LIMIT 10")
    fun getQueries(): List<SearchQuery>

    @Query("DELETE FROM search_history WHERE id = :id")
    fun deleteQuery(id: Int)

    @Query("DELETE FROM search_history")
    fun deleteAll()
}


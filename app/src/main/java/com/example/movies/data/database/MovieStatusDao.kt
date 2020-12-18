package com.example.movies.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movies.data.model.entities.MovieStatus

@Dao
interface MovieStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieStatus(movieState: MovieStatus)

    @Query("SELECT * FROM movies_statuses")
    fun getMovieStatuses(): List<MovieStatus>

    @Query("DELETE FROM movies_statuses WHERE movieId=:id")
    fun deleteMovieStatus(id: Int)

    @Query("DELETE FROM movies_statuses")
    fun deleteAll()
}

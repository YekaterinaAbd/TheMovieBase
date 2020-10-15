package com.example.kino.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kino.data.model.movie.MovieStatus

@Dao
interface MovieStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieStatus(movieState: MovieStatus)

    @Query("SELECT * FROM movies_statuses")
    fun getMovieStatuses(): List<MovieStatus>

    @Query("DELETE FROM movies_statuses")
    fun deleteAll()
}

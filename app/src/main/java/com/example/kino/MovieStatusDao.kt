package com.example.kino

import androidx.room.*
import com.example.kino.MovieClasses.MovieStatus

@Dao
interface MovieStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieStatus(movieState: MovieStatus)

    @Query("SELECT * FROM movies_statuses")
    fun getMovieStatuses(): List<MovieStatus>

    @Query("DELETE FROM movies_statuses")
    fun deleteAll()

    @Delete
    fun deleteMovieStatus(movieStatus: MovieStatus)
}

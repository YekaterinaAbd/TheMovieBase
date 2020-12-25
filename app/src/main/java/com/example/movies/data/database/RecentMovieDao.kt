package com.example.movies.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movies.data.model.entities.RecentMovie

@Dao
interface RecentMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<RecentMovie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentMovie(movie: RecentMovie)

    @Query("SELECT * FROM recent_movies")
    fun getRecentMovies(): List<RecentMovie>?

    @Query("DELETE FROM recent_movies")
    fun deleteAll()
}
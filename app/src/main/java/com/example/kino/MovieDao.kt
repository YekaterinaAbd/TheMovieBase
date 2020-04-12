package com.example.kino

import androidx.room.*
import com.example.kino.MovieClasses.Movie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<Movie>)

    @Query("SELECT * FROM movies")
    fun getMovies(): List<Movie>

    @Query("DELETE FROM movies")
    fun deleteAll()

    @Update
    fun updateMovie(movie: Movie)
}

package com.example.kino

import androidx.room.*
import com.example.kino.MovieClasses.Movie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<Movie>)

    @Query("SELECT * FROM movies ORDER BY voteAverage DESC, voteCount DESC")
    fun getMovies(): List<Movie>

    @Query("DELETE FROM movies")
    fun deleteAll()

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovieById(id: Int): Movie

    @Update
    fun updateMovie(movie: Movie)

    @Query("UPDATE movies SET tagline = :tagline WHERE id = :id")
    fun updateMovieTagline(tagline: String, id: Int)

    @Query("UPDATE movies SET runtime = :runtime WHERE id = :id")
    fun updateMovieRuntime(runtime: Int, id: Int)

}

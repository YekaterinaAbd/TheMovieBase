package com.example.kino.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kino.model.movie.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<Movie>)

    @Query("SELECT * FROM movies ORDER BY voteAverage DESC, voteCount DESC")
    fun getMovies(): List<Movie>

    @Query("SELECT * FROM movies WHERE isClicked=1")
    fun getFavouriteMovies(): List<Movie>

    @Query("SELECT * FROM movies WHERE id=:id")
    fun getMovie(id: Int): Movie

    @Query("UPDATE movies SET tagline = :tagline, runtime = :runtime WHERE id = :id")
    fun updateMovieProperties(tagline: String, runtime: Int, id: Int)

    @Query("UPDATE movies SET isClicked = :isClicked WHERE id = :id")
    fun updateMovieIsCLicked(isClicked: Boolean, id: Int)

    @Query("DELETE FROM movies")
    fun deleteAll()
}

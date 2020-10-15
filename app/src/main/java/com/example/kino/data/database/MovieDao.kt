package com.example.kino.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kino.data.model.movie.LocalMovie

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<LocalMovie>)

    @Query("SELECT * FROM movies ORDER BY voteAverage DESC, voteCount DESC")
    fun getMovies(): List<LocalMovie>

    @Query("SELECT * FROM movies WHERE isClicked=1")
    fun getFavouriteMovies(): List<LocalMovie>

    @Query("SELECT * FROM movies WHERE id=:id")
    fun getMovie(id: Int): LocalMovie

    @Query("UPDATE movies SET tagline = :tagline, runtime = :runtime WHERE id = :id")
    fun updateMovieProperties(tagline: String, runtime: Int, id: Int)

    @Query("UPDATE movies SET isClicked = :isClicked WHERE id = :id")
    fun updateMovieIsCLicked(isClicked: Boolean, id: Int)

    @Query("DELETE FROM movies")
    fun deleteAll()
}

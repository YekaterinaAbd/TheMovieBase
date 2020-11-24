package com.example.kino.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kino.data.model.entities.LocalMovie
import com.example.kino.data.model.movie.MoviesType

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<LocalMovie>)

    @Query("SELECT * FROM movies ORDER BY voteAverage DESC")
    fun getMovies(): List<LocalMovie>

    @Query("SELECT * FROM movies WHERE type = :type")
    fun getMovies(type: String): List<LocalMovie>

    @Query("SELECT * FROM movies WHERE isFavourite=1")
    fun getFavouriteMovies(): List<LocalMovie>

    @Query("UPDATE movies SET isFavourite = :isClicked WHERE id = :id")
    fun updateMovieIsCLicked(isClicked: Boolean, id: Int)

    @Query("DELETE FROM movies")
    fun deleteAll()

    @Query("DELETE FROM movies WHERE type = :type")
    fun deleteAll(type: String)

    @Query("DELETE FROM movies WHERE id=:id AND type = :type")
    fun deleteFromFavourites(id: Int, type: String = MoviesType.FAVOURITES.name)
}

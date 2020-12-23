package com.example.movies.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movies.data.model.entities.LocalMovie
import com.example.movies.domain.model.MoviesType

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

    @Query("SELECT * FROM movies WHERE isInWatchList=1")
    fun getMoviesInWatchList(): List<LocalMovie>

    @Query("UPDATE movies SET isFavourite = :isFavourite WHERE id = :id")
    fun updateMovieIsFavourite(isFavourite: Boolean, id: Int)

    @Query("UPDATE movies SET isInWatchList = :isInWatchList WHERE id = :id")
    fun updateMovieIsInWatchList(isInWatchList: Boolean, id: Int)

    @Query("DELETE FROM movies")
    fun deleteAll()

    @Query("DELETE FROM movies WHERE type = :type")
    fun deleteAll(type: String)

    @Query("DELETE FROM movies WHERE id=:id AND type = :type")
    fun deleteFromFavourites(id: Int, type: String = MoviesType.FAVOURITES.name)

    @Query("DELETE FROM movies WHERE id=:id AND type = :type")
    fun deleteFromWatchList(id: Int, type: String = MoviesType.WATCH_LIST.name)
}

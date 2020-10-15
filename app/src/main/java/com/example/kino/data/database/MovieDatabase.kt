package com.example.kino.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kino.data.model.Marker
import com.example.kino.data.model.movie.LocalMovie
import com.example.kino.data.model.movie.MovieStatus

@Database(entities = [LocalMovie::class, MovieStatus::class, Marker::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun movieStatusDao(): MovieStatusDao
    abstract fun markerDao(): MarkerDao

    companion object {
        private var database: MovieDatabase? = null
        private const val databaseName: String = "movie_database3.db"
        fun getDatabase(context: Context): MovieDatabase {
            if (database == null) {
                database = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    databaseName
                ).build()
            }
            return database!!
        }
    }
}

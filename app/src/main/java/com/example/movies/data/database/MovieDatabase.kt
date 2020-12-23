package com.example.movies.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.movies.data.model.entities.LocalMovie
import com.example.movies.data.model.entities.Marker
import com.example.movies.data.model.entities.MovieStatus
import com.example.movies.data.model.entities.SearchQuery

@Database(
    entities = [LocalMovie::class, MovieStatus::class, Marker::class, SearchQuery::class],
    version = 1
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun movieStatusDao(): MovieStatusDao
    abstract fun markerDao(): MarkerDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        private var database: MovieDatabase? = null
        private const val databaseName: String = "database.db"
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

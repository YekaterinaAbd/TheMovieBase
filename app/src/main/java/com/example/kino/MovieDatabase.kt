package com.example.kino

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kino.MovieClasses.Movie


@Database(entities = [Movie::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        var database: MovieDatabase? = null

        fun getDatabase(context: Context): MovieDatabase {
            if (database == null) {
                database = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "movie_database2.db"
                ).build()
            }
            return database!!
        }
    }
}
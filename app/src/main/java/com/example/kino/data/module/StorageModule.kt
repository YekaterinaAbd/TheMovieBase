package com.example.kino.data.module

import android.content.Context
import android.content.SharedPreferences
import com.example.kino.R
import com.example.kino.data.database.MarkerDao
import com.example.kino.data.database.MovieDao
import com.example.kino.data.database.MovieDatabase
import com.example.kino.data.database.MovieStatusDao
import org.koin.dsl.module

val storageModule = module {
    single { getSharedPreferences(context = get()) }
    single { getMovieDao(context = get()) }
    single { getMovieStatusDao(context = get()) }
    single { getMarkerDao(context = get()) }
}

private fun getMovieDao(context: Context): MovieDao {
    return MovieDatabase.getDatabase(context).movieDao()
}

private fun getMovieStatusDao(context: Context): MovieStatusDao {
    return MovieDatabase.getDatabase(context).movieStatusDao()
}

private fun getMarkerDao(context: Context): MarkerDao {
    return MovieDatabase.getDatabase(context).markerDao()
}

private fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(
        context.getString(R.string.preference_file),
        Context.MODE_PRIVATE
    )
}

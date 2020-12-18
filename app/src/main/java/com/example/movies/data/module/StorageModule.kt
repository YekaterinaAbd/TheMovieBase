package com.example.movies.data.module

import android.content.Context
import android.content.SharedPreferences
import com.example.movies.R
import com.example.movies.data.database.*
import org.koin.dsl.module

val storageModule = module {
    single { getSharedPreferences(context = get()) }
    single { getMovieDao(context = get()) }
    single { getMovieStatusDao(context = get()) }
    single { getMarkerDao(context = get()) }
    single { getSearchHistoryDao(context = get()) }
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

private fun getSearchHistoryDao(context: Context): SearchHistoryDao {
    return MovieDatabase.getDatabase(context).searchHistoryDao()
}

private fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(
        context.getString(R.string.preference_file),
        Context.MODE_PRIVATE
    )
}

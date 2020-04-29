package com.example.kino.view_model

import android.content.Context
import com.example.kino.model.Marker
import com.example.kino.model.database.MarkerDao
import com.example.kino.model.database.MovieDatabase
import com.example.kino.model.generateMarkers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MarkersViewModel(context: Context) : BaseViewModel() {
    private var markerDao: MarkerDao = MovieDatabase.getDatabase(context = context).markerDao()

    fun fillDatabase() {
        launch {
            withContext(Dispatchers.IO) {
                markerDao.deleteAll()
                markerDao.insertAll(generateMarkers())
            }
        }
    }

    fun getMarkers(): List<Marker> = runBlocking {
        return@runBlocking getMarkersFromDatabase()
    }

    private suspend fun getMarkersFromDatabase(): List<Marker> {
        return withContext(Dispatchers.IO) {
            return@withContext markerDao.getMarkers() as MutableList
        }
    }
}

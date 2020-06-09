package com.example.kino.view_model

import com.example.kino.model.Marker
import com.example.kino.model.generateMarkers
import com.example.kino.model.repository.MarkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MarkersViewModel(private val markerRepository: MarkerRepository) :
    BaseViewModel() {

    fun fillDatabase() {
        launch {
            withContext(Dispatchers.IO) {
                markerRepository.deleteMarkers()
                markerRepository.insertMarkers(generateMarkers())
            }
        }
    }

    fun getMarkers(): List<Marker> = runBlocking {
        return@runBlocking getMarkersFromDatabase()
    }

    private suspend fun getMarkersFromDatabase(): List<Marker> {
        return withContext(Dispatchers.IO) {
            return@withContext markerRepository.getMarkers() as MutableList
        }
    }
}

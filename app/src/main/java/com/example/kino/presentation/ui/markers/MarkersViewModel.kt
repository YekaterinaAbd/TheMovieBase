package com.example.kino.presentation.ui.markers

import com.example.kino.data.model.entities.Marker
import com.example.kino.data.model.entities.generateMarkers
import com.example.kino.domain.repository.MarkerRepository
import com.example.kino.presentation.BaseViewModel
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

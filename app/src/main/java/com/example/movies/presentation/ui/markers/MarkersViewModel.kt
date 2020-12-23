package com.example.movies.presentation.ui.markers

import com.example.movies.core.base.BaseViewModel
import com.example.movies.data.model.entities.Marker
import com.example.movies.data.model.entities.generateMarkers
import com.example.movies.domain.repository.MarkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MarkersViewModel(private val markerRepository: MarkerRepository) :
    BaseViewModel() {

    fun fillDatabase() {
        uiScope.launch {
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

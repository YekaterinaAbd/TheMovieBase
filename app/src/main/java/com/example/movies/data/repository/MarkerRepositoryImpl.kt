package com.example.movies.data.repository

import com.example.movies.data.database.MarkerDao
import com.example.movies.data.model.entities.Marker
import com.example.movies.domain.repository.MarkerRepository

class MarkerRepositoryImpl(private var markerDao: MarkerDao) : MarkerRepository {

    override fun insertMarkers(markers: List<Marker>) {
        markerDao.insertAll(markers)
    }

    override fun deleteMarkers() {
        markerDao.deleteAll()
    }

    override fun getMarkers(): List<Marker> {
        return markerDao.getMarkers()
    }
}
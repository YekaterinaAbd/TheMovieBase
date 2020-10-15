package com.example.kino.data.repository

import com.example.kino.data.database.MarkerDao
import com.example.kino.data.model.Marker
import com.example.kino.domain.repository.MarkerRepository

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
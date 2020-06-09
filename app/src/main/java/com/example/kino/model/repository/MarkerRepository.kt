package com.example.kino.model.repository

import com.example.kino.model.Marker
import com.example.kino.model.database.MarkerDao

interface MarkerRepository {
    fun insertMarkers(markers: List<Marker>)
    fun deleteMarkers()
    fun getMarkers(): List<Marker>
}

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
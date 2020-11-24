package com.example.kino.domain.repository

import com.example.kino.data.model.entities.Marker

interface MarkerRepository {
    fun insertMarkers(markers: List<Marker>)
    fun deleteMarkers()
    fun getMarkers(): List<Marker>
}
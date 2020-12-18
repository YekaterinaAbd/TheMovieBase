package com.example.movies.domain.repository

import com.example.movies.data.model.entities.Marker

interface MarkerRepository {
    fun insertMarkers(markers: List<Marker>)
    fun deleteMarkers()
    fun getMarkers(): List<Marker>
}
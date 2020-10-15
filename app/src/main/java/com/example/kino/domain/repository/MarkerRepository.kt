package com.example.kino.domain.repository

import com.example.kino.data.model.Marker

interface MarkerRepository {
    fun insertMarkers(markers: List<Marker>)
    fun deleteMarkers()
    fun getMarkers(): List<Marker>
}
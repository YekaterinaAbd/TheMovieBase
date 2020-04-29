package com.example.kino.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.kino.R
import com.example.kino.model.Marker
import com.example.kino.view_model.MarkersViewModel
import com.example.kino.view_model.ViewModelProviderFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var markersViewModel: MarkersViewModel
    private lateinit var viewModelProviderFactory: ViewModelProviderFactory
    private lateinit var markers: List<Marker>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_maps)
        setViewModel()

        markers = markersViewModel.getMarkers()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setViewModel() {
        viewModelProviderFactory = ViewModelProviderFactory(context = this)
        markersViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MarkersViewModel::class.java)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (!markers.isNullOrEmpty()) {
            for (marker in markers) {
                val mapMarker = LatLng(marker.lat, marker.lng)
                map.addMarker(MarkerOptions().position(mapMarker).title(marker.title))
                map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(marker.lat, marker.lng)))
            }

            val zoomLevel = 11.0f
            val marker = markers[markers.size - 1]
            val latLng = LatLng(marker.lat, marker.lng)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
        }
    }
}

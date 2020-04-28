package com.example.kino.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "markers")
data class Marker(
    @PrimaryKey
    var id: Int,
    val lat: Double,
    val lng: Double,
    val title: String
)

fun generateMarkers(): List<Marker> {
    val markers: MutableList<Marker> = arrayListOf()
    val marker1 = Marker(1, 43.336636, 76.952979, "Arman 3D")
    markers.add(marker1)
    val marker2 = Marker(2, 43.262158, 76.941380, "Lumiera Cinema")
    markers.add(marker2)
    val marker3 = Marker(3, 43.267937, 76.934299, "Illusion ATRIUM")
    markers.add(marker3)
    val marker4 = Marker(4, 43.261043, 76.946448, "Caezar cinema")
    markers.add(marker4)
    val marker5 = Marker(5, 43.240377, 76.905653, "Bekmahanov Cinema")
    markers.add(marker5)
    val marker6 = Marker(6, 43.264207, 76.929469, "Chaplin cinemas")
    markers.add(marker6)
    val marker7 = Marker(7, 43.244536, 76.835891, "Asia park")
    markers.add(marker7)
    val marker8 = Marker(8, 43.233038, 76.955679, "Dostyk cinema")
    markers.add(marker8)
    val marker9 = Marker(9, 43.237548, 76.915060, "Cinema Towers")
    markers.add(marker9)
    val marker10 = Marker(10, 43.265143, 76.899687, "Illusion")
    markers.add(marker10)
    return markers
}

package com.example.kino.data.model.movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class LocalMovie(
    @PrimaryKey
    var id: Int = 0,
    var voteCount: Int? = null,
    var title: String? = null,
    var voteAverage: Double? = null,
    var posterPath: String? = null,
    var releaseDate: String? = null,
    var popularity: String? = null,
    var overview: String? = null,

    var isClicked: Boolean = false,
    var runtime: Int? = null,
    var tagline: String? = null,
    var genreNames: String = ""
)
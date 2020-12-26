package com.example.movies.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.movies.domain.model.MoviesType

@Entity(tableName = "movies")
data class LocalMovie(
    @PrimaryKey(autoGenerate = true)
    var primaryId: Int = 0,
    var id: Int? = 0,
    var type: String? = MoviesType.TOP.type,
    var title: String? = null,
    var voteAverage: Double? = null,
    var posterPath: String? = null,
    var releaseDate: String? = null,
    var isFavourite: Boolean = false,
    var isInWatchList: Boolean = false,
    var genreNames: String = ""
)

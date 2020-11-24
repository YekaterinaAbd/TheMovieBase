package com.example.kino.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.kino.data.model.movie.MoviesType

@Entity(tableName = "movies")
data class LocalMovie(
    @PrimaryKey(autoGenerate = true)
    var primaryId: Int = 0,
    var id: Int = 0,
    var type: String? = MoviesType.typeToString(MoviesType.TOP),
    var title: String? = null,
    var voteAverage: Double? = null,
    var posterPath: String? = null,
    var releaseDate: String? = null,
    var isFavourite: Boolean = false,
    var isInWatchList: Boolean = false,
    var genreNames: String = ""
)

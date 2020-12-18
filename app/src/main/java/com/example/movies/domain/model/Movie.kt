package com.example.movies.domain.model

import com.example.movies.data.model.movie.Genre
import java.io.Serializable

data class Movie(
    var id: Int = 0,
    var voteCount: Int? = null,
    var title: String? = null,
    var voteAverage: Double? = null,
    var posterPath: String? = null,
    var releaseDate: String? = null,
    var popularity: String? = null,
    var overview: String? = null,
    var isFavourite: Boolean = false,
    var isInWatchList: Boolean = false,
    var runtime: Int? = null,
    var tagline: String? = null,
    var genreNames: String = "",
    var position: Int = 0,
    var genreIds: ArrayList<Int>? = null,
    var type: String? = null,
    val genres: List<Genre>? = null
) : Serializable
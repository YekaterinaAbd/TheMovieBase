package com.example.movies.domain.model

import com.example.movies.data.model.movie.Genre
import java.io.Serializable

data class Movie(
    val id: Int?,
    val voteCount: Int? = null,
    val title: String? = null,
    val voteAverage: Double? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val popularity: Double? = null,
    val overview: String? = null,
    var isFavourite: Boolean = false,
    var isInWatchList: Boolean = false,
    val runtime: Int? = null,
    val tagline: String? = null,
    var genreNames: String = "",
    var position: Int = 0,
    val genreIds: ArrayList<Int>? = null,
    var type: String? = null,
    val genres: List<Genre>? = null,
    var rating: Double? = null
) : Serializable
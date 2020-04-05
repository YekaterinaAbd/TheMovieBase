package com.example.kino.MovieClasses
import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("title") val title: String,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("genre_ids") val genres: List<Int>,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("popularity") val popularity: String,
    var genreNames: MutableList<String>,
    var number: Int = 0,
    var isClicked:Boolean=false
)

package com.example.kino.MovieClasses
import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("vote_count") val vote_count: Int,
    @SerializedName("title") val title: String,
    @SerializedName("vote_average") val vote_average: Float,
    @SerializedName("poster_path") val poster_path: String,
    @SerializedName("genre_ids") val genres: List<Int>,
    @SerializedName("release_date") val release_date: String,
    @SerializedName("popularity") val popularity: String,
    var genre_names: MutableList<String>,
    var number: Int = 0,
    var isClicked:Boolean=false
)

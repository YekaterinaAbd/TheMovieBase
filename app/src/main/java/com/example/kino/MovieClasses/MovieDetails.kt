package com.example.kino.MovieClasses

import com.google.gson.annotations.SerializedName

data class MovieDetails(
    @SerializedName("id") val id: Int,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("title") val title: String,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("genres") val genres: List<Genre>? = null,
    @SerializedName("runtime") val runtime: Int = 0,

    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("budget") val budget: Int = 0,
    @SerializedName("overview") val overview: String,
    @SerializedName("tagline") val tagline: String = "",
    @SerializedName("production_companies") val productionCompanies: List<Company>? = null
)


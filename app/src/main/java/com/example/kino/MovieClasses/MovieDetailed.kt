package com.example.kino.MovieClasses

import com.google.gson.annotations.SerializedName

data class MovieDetailed (
    @SerializedName("id") val id: Int,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("title") val title: String,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("runtime") val runtime: Int,

    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("budget") val budget: Int,
    @SerializedName("overview") val overview: String,
    @SerializedName("tagline") val tagline: String,
    @SerializedName("production_companies") val productionCompanies: List<Company>
)


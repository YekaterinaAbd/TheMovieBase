package com.example.movies.data.model.movie

import com.google.gson.annotations.SerializedName

data class RemoteMovie(
    @SerializedName("id") val id: Int?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("popularity") val popularity: Double?,
    @SerializedName("title") val title: String?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("genre_ids") val genreIds: ArrayList<Int>?,
    @SerializedName("rating") val rating: Double?
)


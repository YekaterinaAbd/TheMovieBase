package com.example.movies.data.model.movie

import com.google.gson.annotations.SerializedName

data class Genres(
    @SerializedName("genres") val genres: List<Genre>
)

data class Genre(
    @SerializedName("id") val genreId: Int,
    @SerializedName("name") val genre: String
)

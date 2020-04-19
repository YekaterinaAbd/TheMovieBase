package com.example.kino.model.movie

import com.google.gson.annotations.SerializedName

data class Genre(
    @SerializedName("id") val genreId: Int,
    @SerializedName("name") val genre: String

)
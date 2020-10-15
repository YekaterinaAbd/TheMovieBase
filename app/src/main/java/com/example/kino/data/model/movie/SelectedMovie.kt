package com.example.kino.data.model.movie

import com.google.gson.annotations.SerializedName

data class SelectedMovie(
    @SerializedName("media_type") val mediaType: String = "movie",
    @SerializedName("media_id") val movieId: Int,
    @SerializedName("favorite") var selectedStatus: Boolean
)

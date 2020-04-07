package com.example.kino.MovieClasses
import com.google.gson.annotations.SerializedName

data class MovieStatus(
    @SerializedName("id") val movieId: Int,
    @SerializedName("favorite") val selectedStatus: Boolean
)

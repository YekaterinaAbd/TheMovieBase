package com.example.kino.MovieClasses
import com.google.gson.annotations.SerializedName

data class MovieStatsResponse(
    @SerializedName("id") val movieId: Int,
    @SerializedName("favorite") val favourite: Boolean
)

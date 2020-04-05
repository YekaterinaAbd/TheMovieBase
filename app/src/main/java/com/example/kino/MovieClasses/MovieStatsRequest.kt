package com.example.kino.MovieClasses
import com.google.gson.annotations.SerializedName

data class MovieStatsRequest(
    @SerializedName("movie_id") val movie_id: Int,
    @SerializedName("api_key") val api_key:String,
    @SerializedName("session_id") val session_id:String
)

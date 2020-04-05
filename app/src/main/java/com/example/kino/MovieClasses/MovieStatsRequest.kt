package com.example.kino.MovieClasses
import com.google.gson.annotations.SerializedName

data class MovieStatsRequest(
    @SerializedName("movie_id") val movieId: Int,
    @SerializedName("api_key") val apiKey:String,
    @SerializedName("session_id") val sessionId:String
)

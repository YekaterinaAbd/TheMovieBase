package com.example.kino.MovieClasses

import com.google.gson.annotations.SerializedName

data class FavouritesRequest (
    @SerializedName("media_type") val media_type: String = "movie",
    @SerializedName("media_id") val movie_id:Int,
    @SerializedName("favorite") var favourite: Boolean
)
package com.example.kino.MovieClasses

import com.google.gson.annotations.SerializedName

data class FavouritesResponse (
    @SerializedName("status_message") val status_message: String
)
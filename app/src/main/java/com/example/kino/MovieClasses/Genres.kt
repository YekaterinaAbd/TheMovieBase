package com.example.kino.MovieClasses

import com.google.gson.annotations.SerializedName

data class Genres (
    @SerializedName("genres") val genres: List<Genre>
)
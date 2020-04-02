package com.example.kino.MovieClasses

import com.google.gson.annotations.SerializedName

data class GenreResults (
    @SerializedName("genres") val genres: List<Genre>
)
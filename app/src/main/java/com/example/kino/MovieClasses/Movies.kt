package com.example.kino.MovieClasses

import com.google.gson.annotations.SerializedName

data class Movies(
    @SerializedName("results") val movieList: List<Movie>
)
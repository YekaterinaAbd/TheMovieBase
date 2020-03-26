package com.example.kino.MovieClasses

import com.example.kino.MovieClasses.Movie
import com.google.gson.annotations.SerializedName

data class MovieResults(
    @SerializedName("results") val results: List<Movie>
)
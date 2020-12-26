package com.example.movies.data.model.movie.response

import com.example.movies.data.model.movie.RemoteMovie
import com.google.gson.annotations.SerializedName

data class Movies(
    @SerializedName("results") val movieList: List<RemoteMovie>,
    @SerializedName("page") val page: Int,
    @SerializedName("total_pages") val totalPages: Int?
)

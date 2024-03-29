package com.example.movies.data.model.movie

import com.google.gson.annotations.SerializedName

data class FavouriteMovie(
    @SerializedName("media_type") val mediaType: String = "movie",
    @SerializedName("media_id") val movieId: Int,
    @SerializedName("favorite") var favourite: Boolean
)

data class WatchListMovie(
    @SerializedName("media_type") val mediaType: String = "movie",
    @SerializedName("media_id") val movieId: Int,
    @SerializedName("watchlist") var watchlist: Boolean
)



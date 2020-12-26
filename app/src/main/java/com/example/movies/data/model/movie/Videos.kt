package com.example.movies.data.model.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Videos(
    @SerializedName("id") var id: Int?,
    @SerializedName("results") var results: List<Video>?
) : Serializable


data class Video(
    @SerializedName("id") var id: String?,
    @SerializedName("key") var link: String?,
    @SerializedName("name") var videoTitle: String?
) : Serializable


package com.example.kino.data.model.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Videos(

    @SerializedName("id") var id: Int,
    @SerializedName("results") var results: List<Video>? = null

) : Serializable


data class Video(

    @SerializedName("id") var id: String? = null,
    @SerializedName("key") var link: String? = null,
    @SerializedName("name") var videoTitle: String? = null

) : Serializable


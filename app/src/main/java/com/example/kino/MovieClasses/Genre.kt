package com.example.kino.MovieClasses

import com.google.gson.annotations.SerializedName

data class Genre (
    @SerializedName("id") val genre_id: Int,
    @SerializedName("name") val genre: String

)
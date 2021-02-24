package com.example.movies.data.model.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Keywords(
    @SerializedName("id") val movieId: Int?,
    @SerializedName("keywords") val keywordsList: List<Keyword>?
) : Serializable

data class Keyword(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val keyword: String?
) : Serializable


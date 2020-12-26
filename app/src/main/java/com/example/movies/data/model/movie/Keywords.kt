package com.example.movies.data.model.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Keywords(
    @SerializedName("id") var movieId: Int?,
    @SerializedName("keywords") var keywordsList: List<KeyWord>?
) : Serializable

data class KeyWord(
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var keyword: String?
) : Serializable


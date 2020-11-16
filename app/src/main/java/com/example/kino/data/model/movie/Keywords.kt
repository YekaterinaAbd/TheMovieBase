package com.example.kino.data.model.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Keywords(
    @SerializedName("id") var movieId: Int? = null,
    @SerializedName("keywords") var keywordsList: List<KeyWord>? = emptyList()
) : Serializable

data class KeyWord(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var keyword: String? = null
) : Serializable


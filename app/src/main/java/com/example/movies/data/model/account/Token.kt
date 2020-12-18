package com.example.movies.data.model.account

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("request_token") val token: String
)

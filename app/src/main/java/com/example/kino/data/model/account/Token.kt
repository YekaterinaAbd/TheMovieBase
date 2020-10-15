package com.example.kino.data.model.account

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("request_token") val token: String
)

package com.example.kino.AccountClasses

import com.google.gson.annotations.SerializedName

data class TokenResult (
    @SerializedName("request_token") val requestToken: String
    )

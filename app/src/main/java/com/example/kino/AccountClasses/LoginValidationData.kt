package com.example.kino.AccountClasses

import com.google.gson.annotations.SerializedName

data class LoginValidationData (
    @SerializedName("username") var username: String,
    @SerializedName("password") var password: String,
    @SerializedName("request_token") var requestToken: String
)
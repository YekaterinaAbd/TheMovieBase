package com.example.kino.model.account

import com.google.gson.annotations.SerializedName

data class Session(
    @SerializedName("session_id") val sessionId: String
)

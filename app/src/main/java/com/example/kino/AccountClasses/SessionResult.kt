package com.example.kino.AccountClasses

import com.google.gson.annotations.SerializedName

data class SessionResult (
    @SerializedName("session_id") val sessionId: String
)

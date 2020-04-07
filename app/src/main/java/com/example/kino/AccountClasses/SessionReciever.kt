package com.example.kino.AccountClasses

import com.google.gson.annotations.SerializedName

data class SessionReciever (
    @SerializedName("session_id") val sessionId: String
)

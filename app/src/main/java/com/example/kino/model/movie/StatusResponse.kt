package com.example.kino.model.movie

import com.google.gson.annotations.SerializedName

data class StatusResponse(
    @SerializedName("status_message") val statusMessage: String
)
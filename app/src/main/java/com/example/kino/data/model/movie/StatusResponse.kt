package com.example.kino.data.model.movie

import com.google.gson.annotations.SerializedName

data class StatusResponse(
    @SerializedName("status_message") val statusMessage: String
)

package com.example.kino.MovieClasses

import com.google.gson.annotations.SerializedName

data class StatusResponse(
    @SerializedName("status_message") val statusMessage: String
)
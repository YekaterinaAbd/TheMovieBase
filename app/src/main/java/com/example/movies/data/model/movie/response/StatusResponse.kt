package com.example.movies.data.model.movie.response

import com.google.gson.annotations.SerializedName

data class StatusResponse(
    @SerializedName("status_message") val statusMessage: String
)

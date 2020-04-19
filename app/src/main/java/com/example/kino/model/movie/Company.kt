package com.example.kino.model.movie

import com.google.gson.annotations.SerializedName

data class Company(

    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)


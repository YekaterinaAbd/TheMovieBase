package com.example.kino.MovieClasses

import com.google.gson.annotations.SerializedName

data class Company(

    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)


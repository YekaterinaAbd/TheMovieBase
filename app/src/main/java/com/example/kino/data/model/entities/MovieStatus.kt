package com.example.kino.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies_statuses")
data class MovieStatus(
    @PrimaryKey
    @SerializedName("id") val movieId: Int,
    @SerializedName("favorite") var selectedStatus: Boolean
)

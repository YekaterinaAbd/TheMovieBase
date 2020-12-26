package com.example.movies.data.model.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies_statuses")
data class MovieStatus(
    @PrimaryKey
    @SerializedName("id") var id: Int,
    @SerializedName("favorite") var favourite: Boolean,
    @SerializedName("watchlist") var watchlist: Boolean,
    var type: String,
    @Ignore
    @SerializedName("rated")
    val rated: Any? = null
) {
    constructor() : this(0, false, false, "")
}


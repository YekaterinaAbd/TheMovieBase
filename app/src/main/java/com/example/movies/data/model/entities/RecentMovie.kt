package com.example.movies.data.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "recent_movies", indices = [Index(value = ["query"], unique = true)])
data class RecentMovie(
    @PrimaryKey(autoGenerate = true)
    val entityId: Int = 0,
    @ColumnInfo(name = "query")
    val id: Int? = null,
    val title: String? = null,
    val releaseDate: String? = null,
    val posterPath: String? = null,
    val voteAverage: Double? = null,
    val genres: String? = null
)
package com.example.kino.MovieClasses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    @SerializedName("id") var id: Int,
    @SerializedName("vote_count") var voteCount: Int,
    @SerializedName("title") var title: String,
    @SerializedName("vote_average") var voteAverage: Float,
    @SerializedName("poster_path") var posterPath: String,
    @SerializedName("release_date") var releaseDate: String,
    @SerializedName("popularity") var popularity: String,
    // var genreNames: MutableList<String>?,
    var position: Int = 0,
    var isClicked: Boolean


    //@SerializedName("genre_ids")
    //@TypeConverters(DataTypeConverter::class)
    //var genres: List<Int>
)





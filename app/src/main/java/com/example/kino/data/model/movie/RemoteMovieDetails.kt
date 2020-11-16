package com.example.kino.data.model.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RemoteMovieDetails(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("vote_count") var voteCount: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("vote_average") var voteAverage: Double? = null,
    @SerializedName("poster_path") var posterPath: String? = null,
    @SerializedName("release_date") var releaseDate: String? = null,
    @SerializedName("popularity") var popularity: String? = null,
    @SerializedName("overview") var overview: String? = null,
    @SerializedName("genre_ids") var genreIds: ArrayList<Int>? = null,
    @SerializedName("adult") var adult: Boolean? = false,
    @SerializedName("budget") var budget: Int? = 0,
    @SerializedName("runtime") var runtime: Int? = null,
    @SerializedName("tagline") var tagLine: String? = null,
    @SerializedName("genres") var genres: List<Genre> = emptyList(),

    var genreNames: String = "",
    var isClicked: Boolean = false

) : Serializable


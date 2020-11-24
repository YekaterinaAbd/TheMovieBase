package com.example.kino.data.model.movie

import com.google.gson.annotations.SerializedName

data class RemoteMovie(

    @SerializedName("id") var id: Int = 0,
    @SerializedName("vote_count") var voteCount: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("vote_average") var voteAverage: Double? = null,
    @SerializedName("poster_path") var posterPath: String? = null,
    @SerializedName("release_date") var releaseDate: String? = null,
    @SerializedName("popularity") var popularity: String? = null,
    @SerializedName("overview") var overview: String? = null,
    @SerializedName("genre_ids") var genreIds: ArrayList<Int>? = null
)


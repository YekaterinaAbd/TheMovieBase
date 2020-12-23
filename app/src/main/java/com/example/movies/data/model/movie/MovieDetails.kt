package com.example.movies.data.model.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MovieDetails(

    @SerializedName("id") val id: Int?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("popularity") val popularity: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("genre_ids") var genreIds: ArrayList<Int>?,
    @SerializedName("adult") val adult: Boolean?,
    @SerializedName("budget") val budget: Int?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("tagline") val tagLine: String?,
    @SerializedName("genres") val genres: List<Genre>?,
    @SerializedName("production_countries") val countries: List<Country>?,

    var favourite: Boolean = false,
    var watchlist: Boolean = false,
    var userRating: Double? = null

) : Serializable

data class Country(
    @SerializedName("iso_3166_1") val shortName: String?,
    @SerializedName("name") val country: String?
)


package com.example.movies.data.mapper

import com.example.movies.data.model.movie.RemoteMovie
import com.example.movies.domain.Mapper
import com.example.movies.domain.model.Movie

class RemoteMovieMapper : Mapper<RemoteMovie, Movie> {

    override fun from(model: RemoteMovie): Movie = with(model) {
        return Movie(
            id = id,
            voteCount = voteCount,
            title = title,
            voteAverage = voteAverage,
            posterPath = posterPath,
            releaseDate = releaseDate,
            popularity = popularity,
            overview = overview,
            isFavourite = false,
            isInWatchList = false,
            runtime = null,
            tagline = null,
            genreIds = genreIds,
            genreNames = "",
            position = 0,
            genres = null
        )
    }

    override fun to(model: Movie): RemoteMovie = with(model) {
        return RemoteMovie(
            id = id,
            voteCount = voteCount,
            title = title,
            voteAverage = voteAverage,
            posterPath = posterPath,
            releaseDate = releaseDate,
            popularity = popularity,
            overview = overview,
            genreIds = genreIds,
            rating = rating
        )
    }
}

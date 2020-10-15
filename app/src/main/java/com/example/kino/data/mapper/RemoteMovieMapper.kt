package com.example.kino.data.mapper

import com.example.kino.data.model.movie.RemoteMovie
import com.example.kino.domain.model.Movie

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
            isClicked = false,
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
            genreIds = genreIds
        )
    }
}
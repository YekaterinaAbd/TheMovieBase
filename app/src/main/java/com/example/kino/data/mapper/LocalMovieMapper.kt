package com.example.kino.data.mapper

import com.example.kino.data.model.movie.LocalMovie
import com.example.kino.domain.model.Movie

class LocalMovieMapper : Mapper<LocalMovie, Movie> {
    override fun from(model: LocalMovie): Movie = with(model) {
        return Movie(
            id = id,
            voteCount = voteCount,
            title = title,
            voteAverage = voteAverage,
            posterPath = posterPath,
            releaseDate = releaseDate,
            popularity = popularity,
            overview = overview,
            isClicked = isClicked,
            runtime = runtime,
            tagline = tagline,
            genreIds = null,
            genreNames = genreNames,
            position = 0,
            genres = null
        )
    }

    override fun to(model: Movie): LocalMovie = with(model) {
        return LocalMovie(
            id = id,
            voteCount = voteCount,
            title = title,
            voteAverage = voteAverage,
            posterPath = posterPath,
            releaseDate = releaseDate,
            popularity = popularity,
            overview = overview,
            isClicked = isClicked,
            runtime = runtime,
            tagline = tagline,
            genreNames = genreNames
        )
    }
}
package com.example.kino.data.mapper

import com.example.kino.data.model.entities.LocalMovie
import com.example.kino.domain.model.Movie

class LocalMovieMapper : Mapper<LocalMovie, Movie> {
    override fun from(model: LocalMovie): Movie = with(model) {
        return Movie(
            id = id,
            title = title,
            voteAverage = voteAverage,
            posterPath = posterPath,
            releaseDate = releaseDate,
            isFavourite = isFavourite,
            isInWatchList = isInWatchList,
            genreIds = null,
            genreNames = genreNames,
            position = 0,
            genres = null,
            type = type
        )
    }

    override fun to(model: Movie): LocalMovie = with(model) {
        return LocalMovie(
            id = id,
            title = title,
            voteAverage = voteAverage,
            posterPath = posterPath,
            releaseDate = releaseDate,
            isFavourite = isFavourite,
            genreNames = genreNames,
            type = type
        )
    }
}
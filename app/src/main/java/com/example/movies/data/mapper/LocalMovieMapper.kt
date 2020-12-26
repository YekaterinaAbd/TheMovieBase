package com.example.movies.data.mapper

import com.example.movies.data.model.entities.LocalMovie
import com.example.movies.domain.Mapper
import com.example.movies.domain.model.Movie

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
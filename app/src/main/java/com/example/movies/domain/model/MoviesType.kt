package com.example.movies.domain.model

enum class MoviesType(val type: String) {
    TOP("Top Rated"),
    CURRENT("Current Playing"),
    UPCOMING("Upcoming"),
    POPULAR("Popular"),
    FAVOURITES("Favourite"),
    WATCH_LIST("Watch List"),
    RATED("Rated")
}

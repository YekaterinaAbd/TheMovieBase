package com.example.movies.data.model.movie

enum class MoviesType {
    TOP,
    CURRENT,
    UPCOMING,
    POPULAR,
    FAVOURITES,
    WATCH_LIST;

    companion object {
        fun typeToString(type: MoviesType): String {
            return when (type) {
                TOP -> "Top Rated"
                CURRENT -> "Current Playing"
                UPCOMING -> "Upcoming"
                POPULAR -> "Popular"
                FAVOURITES -> "Favourite"
                WATCH_LIST -> "Watch List"
            }
        }
    }
}

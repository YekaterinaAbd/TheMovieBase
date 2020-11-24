package com.example.kino.data.model.movie

enum class MoviesType {
    TOP,
    CURRENT,
    UPCOMING,
    FAVOURITES,
    WATCH_LIST;

    companion object {
        fun typeToString(type: MoviesType): String {
            return when (type) {
                TOP -> "Top Rated"
                CURRENT -> "Current Playing"
                UPCOMING -> "Upcoming"
                FAVOURITES -> "Favourite"
                WATCH_LIST -> "Watch List"
            }
        }
    }
}

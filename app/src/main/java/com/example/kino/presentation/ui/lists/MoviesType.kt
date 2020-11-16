package com.example.kino.presentation.ui.lists

enum class MoviesType {
    TOP,
    CURRENT_PLAYING,
    UPCOMING,
    FAVOURITES;

    companion object {
        fun typeToString(type: MoviesType): String {
            return when (type) {
                TOP -> "Top Rated"
                CURRENT_PLAYING -> "Current Playing"
                UPCOMING -> "Upcoming"
                FAVOURITES -> "Favourite"
            }
        }
    }
}

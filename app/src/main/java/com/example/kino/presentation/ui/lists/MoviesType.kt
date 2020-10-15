package com.example.kino.presentation.ui.lists

enum class MoviesType {
    TOP,
    CURRENT_PLAYING,
    FAVOURITES;

    companion object {
        fun typeToString(type: MoviesType): String {
            return when (type) {
                TOP -> "Top Rated"
                CURRENT_PLAYING -> "Current Playing"
                FAVOURITES -> "Favourite"
            }
        }
    }
}

package com.example.movies.presentation.ui.lists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movies.domain.model.Movie

class SharedViewModel : ViewModel() {
    val liked = MutableLiveData<Movie>()

    fun setMovie(movie: Movie) {
        liked.value = movie
    }
}

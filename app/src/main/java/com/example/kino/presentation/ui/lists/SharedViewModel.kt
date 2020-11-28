package com.example.kino.presentation.ui.lists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.domain.model.Movie

class SharedViewModel : ViewModel() {
    val liked = MutableLiveData<Movie>()

    fun setMovie(movie: Movie) {
        liked.value = movie
    }
}

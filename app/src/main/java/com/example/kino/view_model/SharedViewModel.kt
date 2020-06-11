package com.example.kino.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.model.movie.Movie

class SharedViewModel: ViewModel() {
    val liked = MutableLiveData<Movie>()

    fun setMovie(movie:Movie){
        liked.value = movie
    }

}
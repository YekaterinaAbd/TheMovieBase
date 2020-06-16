package com.example.kino.view_model

import android.content.Context
import com.example.kino.model.repository.MovieRepository

interface Factory {
    fun create(): MoviesListViewModel
}

class MovieListViewModelFactory(private val context: Context, private val userRepository: MovieRepository) : Factory {
    override fun create(): MoviesListViewModel {
        return MoviesListViewModel(context, userRepository)
    }
}
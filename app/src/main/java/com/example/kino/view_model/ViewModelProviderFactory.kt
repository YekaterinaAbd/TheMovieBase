package com.example.kino.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kino.model.repository.MarkerRepository
import com.example.kino.model.repository.MovieRepository

class ViewModelFactory(
    private val context: Context,
    private val movieRepository: MovieRepository,
    private val markerRepository: MarkerRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        var viewModel: ViewModel? = null
        if (modelClass.isAssignableFrom(MoviesListViewModel::class.java))
            viewModel = MoviesListViewModel(context, movieRepository)
        else if (modelClass.isAssignableFrom(MarkersViewModel::class.java))
            viewModel = MarkersViewModel(markerRepository)

        return viewModel as T
    }
}

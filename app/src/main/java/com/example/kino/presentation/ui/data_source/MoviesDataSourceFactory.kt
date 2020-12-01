package com.example.kino.presentation.ui.data_source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.kino.data.model.movie.MoviesType
import com.example.kino.domain.model.Movie
import com.example.kino.domain.use_case.MoviesListsUseCase
import kotlinx.coroutines.CoroutineScope

class MoviesDataSourceFactory(
    private val scope: CoroutineScope,
    private val context: Context,
    private val moviesListsUseCase: MoviesListsUseCase,
    private val sessionId: String,
    private val movieType: MoviesType
) : DataSource.Factory<Int, Movie>() {

    val liveData = MutableLiveData<MoviesDataSource>()
    private lateinit var moviesDataSource: MoviesDataSource

    override fun create(): DataSource<Int, Movie> {
        moviesDataSource = MoviesDataSource(
            scope = scope,
            context = context,
            moviesListsUseCase = moviesListsUseCase,
            sessionId = sessionId,
            movieType = movieType
        )
        liveData.postValue(moviesDataSource)
        return moviesDataSource
    }

    fun invalidate() {
        moviesDataSource.invalidate()
    }
}
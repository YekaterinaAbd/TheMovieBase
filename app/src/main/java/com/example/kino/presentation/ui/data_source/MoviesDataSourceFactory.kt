package com.example.kino.presentation.ui.data_source

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.kino.domain.model.Movie
import com.example.kino.domain.use_case.MoviesListsUseCase
import kotlin.coroutines.CoroutineContext

class MoviesDataSourceFactory(
    private val moviesLists: MoviesListsUseCase,
    private val coroutineContext: CoroutineContext,
    private val query: String? = null
) : DataSource.Factory<Int, Movie>() {

    val liveData = MutableLiveData<MoviesDataSource>()
    private lateinit var moviesDataSource: MoviesDataSource

    override fun create(): DataSource<Int, Movie> {
        moviesDataSource = MoviesDataSource(
            moviesLists = moviesLists,
            coroutineContext = coroutineContext,
            query = query
        )
        liveData.postValue(moviesDataSource)
        return moviesDataSource
    }

    fun invalidate() {
        moviesDataSource.invalidate()
    }
}
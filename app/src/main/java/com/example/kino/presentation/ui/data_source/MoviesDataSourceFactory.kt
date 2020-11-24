package com.example.kino.presentation.ui.data_source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.kino.domain.model.Movie
import com.example.kino.domain.use_case.SearchMoviesUseCase
import kotlin.coroutines.CoroutineContext

class MoviesDataSourceFactory(
    private val coroutineContext: CoroutineContext,
    private val searchUseCase: SearchMoviesUseCase,
    private val query: String? = null,
    private val context: Context
) : DataSource.Factory<Int, Movie>() {

    val liveData = MutableLiveData<SearchDataSource>()
    private lateinit var moviesDataSource: SearchDataSource

    override fun create(): DataSource<Int, Movie> {
        moviesDataSource = SearchDataSource(
            searchUseCase = searchUseCase,
            coroutineContext = coroutineContext,
            query = query,
            context = context
        )
        liveData.postValue(moviesDataSource)
        return moviesDataSource
    }

    fun invalidate() {
        moviesDataSource.invalidate()
    }
}
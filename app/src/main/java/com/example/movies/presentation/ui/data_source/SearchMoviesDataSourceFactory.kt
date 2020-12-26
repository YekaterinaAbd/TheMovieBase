package com.example.movies.presentation.ui.data_source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.movies.domain.model.Movie
import com.example.movies.domain.use_case.SearchMoviesUseCase
import kotlinx.coroutines.CoroutineScope

class SearchMoviesDataSourceFactory(
    private val scope: CoroutineScope,
    private val searchUseCase: SearchMoviesUseCase,
    private val query: String? = null,
    private val context: Context
) : DataSource.Factory<Int, Movie>() {

    val liveData = MutableLiveData<SearchDataSource>()
    private lateinit var searchDataSource: SearchDataSource

    override fun create(): DataSource<Int, Movie> {
        searchDataSource = SearchDataSource(
            searchUseCase = searchUseCase,
            scope = scope,
            query = query,
            context = context
        )
        liveData.postValue(searchDataSource)
        return searchDataSource
    }

    fun invalidate() {
        searchDataSource.invalidate()
    }
}
package com.example.movies.presentation.ui.data_source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.movies.domain.model.Movie
import com.example.movies.domain.use_case.SearchMoviesUseCase
import kotlinx.coroutines.CoroutineScope

class DiscoverDataSourceFactory(
    private val context: Context,
    private val scope: CoroutineScope,
    private val searchUseCase: SearchMoviesUseCase,
    private val genres: String?,
    private val keywords: String?
) : DataSource.Factory<Int, Movie>() {

    val liveData = MutableLiveData<DiscoverDataSource>()
    private lateinit var discoverDataSource: DiscoverDataSource

    override fun create(): DataSource<Int, Movie> {
        discoverDataSource = DiscoverDataSource(
            context = context,
            scope = scope,
            searchUseCase = searchUseCase,
            genres = genres,
            keywords = keywords
        )
        liveData.postValue(discoverDataSource)
        return discoverDataSource
    }

    fun invalidate() {
        discoverDataSource.invalidate()
    }
}
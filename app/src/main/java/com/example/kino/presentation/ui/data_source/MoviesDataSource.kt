package com.example.kino.presentation.ui.data_source

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.kino.domain.model.Movie
import com.example.kino.domain.use_case.MoviesListsUseCase
import com.example.kino.presentation.ui.MovieState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MoviesDataSource(
    private val moviesLists: MoviesListsUseCase,
    coroutineContext: CoroutineContext,
    private val query: String? = null
) : PageKeyedDataSource<Int, Movie>() {

    companion object {
        const val PAGE_SIZE = 20
        private const val FIRST_PAGE = 1
    }

    private var _state = MutableLiveData<MovieState>()

    private val job = Job()
    private val scope = CoroutineScope(coroutineContext + job)

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        updateState(MovieState.ShowLoading)
        scope.launch {
            try {
                val response = moviesLists.searchMovies(query, FIRST_PAGE) ?: emptyList()
                callback.onResult(response, null, FIRST_PAGE + 1)
            } catch (e: Exception) {
                updateState(MovieState.Error(e.message))
            }
            updateState(MovieState.HideLoading)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        scope.launch {
            if (params.key != FIRST_PAGE) {
                updateState(MovieState.ShowPageLoading)
            } else {
                updateState(MovieState.HideLoading)
            }
            try {
                val response = moviesLists.searchMovies(query, params.key) ?: emptyList()
                val key = if (response.isNotEmpty()) params.key + 1 else null
                callback.onResult(response, key)
            } catch (e: Exception) {
                updateState(MovieState.Error(e.message))
            }
            updateState(MovieState.HidePageLoading)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {}

    private fun updateState(state: MovieState) {
        getState().postValue(state)
    }

    fun getState() = _state
}
package com.example.movies.presentation.ui.data_source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.movies.domain.model.Movie
import com.example.movies.domain.use_case.SearchMoviesUseCase
import com.example.movies.presentation.model.GenresList
import com.example.movies.presentation.ui.MovieState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SearchDataSource(
    private val scope: CoroutineScope,
    private val searchUseCase: SearchMoviesUseCase,
    private val query: String? = null,
    private val context: Context
) : PageKeyedDataSource<Int, Movie>() {

    companion object {
        const val PAGE_SIZE = 20
        private const val FIRST_PAGE = 1
    }

    init {
        GenresList.getGenres()
    }

    private var _state = MutableLiveData<MovieState>()

//    private val job = Job()
//    private val scope = CoroutineScope(coroutineContext + job)

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        updateState(MovieState.ShowLoading)
        scope.launch {
            try {
                val response = searchUseCase.searchMovies(query, FIRST_PAGE) ?: emptyList()
                for (movie in response) {
                    GenresList.setMovieGenres(movie, context)
                }
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
                val response = searchUseCase.searchMovies(query, params.key) ?: emptyList()
                for (movie in response) {
                    GenresList.setMovieGenres(movie, context)
                }
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

    override fun invalidate() {
        super.invalidate()
        scope.cancel()
    }
}
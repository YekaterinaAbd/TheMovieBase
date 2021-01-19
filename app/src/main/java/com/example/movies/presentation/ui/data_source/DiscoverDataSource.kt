package com.example.movies.presentation.ui.data_source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.movies.domain.model.Movie
import com.example.movies.domain.use_case.SearchMoviesUseCase
import com.example.movies.presentation.model.GenresList
import com.example.movies.presentation.ui.LoadingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DiscoverDataSource(
    private val context: Context,
    private val scope: CoroutineScope,
    private val searchUseCase: SearchMoviesUseCase,
    private val genres: String?,
    private val keywords: String?
) : PageKeyedDataSource<Int, Movie>() {

    companion object {
        const val PAGE_SIZE = 20
        private const val FIRST_PAGE = 1
    }

    init {
        GenresList.getGenres()
    }

    private var _state = MutableLiveData<LoadingState>()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        updateState(LoadingState.ShowLoading)
        scope.launch {
            try {
                val response =
                    searchUseCase.discoverMovies(FIRST_PAGE, genres, keywords) ?: emptyList()
                for (movie in response) {
                    GenresList.setMovieGenres(context, movie)
                }
                callback.onResult(response, null, FIRST_PAGE + 1)
            } catch (e: Exception) {
                updateState(LoadingState.Error(e.message))
            }
            updateState(LoadingState.HideLoading)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        scope.launch {
            if (params.key != FIRST_PAGE) {
                updateState(LoadingState.ShowPageLoading)
            } else {
                updateState(LoadingState.HideLoading)
            }
            try {
                val response =
                    searchUseCase.discoverMovies(params.key, genres, keywords) ?: emptyList()
                for (movie in response) {
                    GenresList.setMovieGenres(context, movie)
                }

                val key = if (response.isNotEmpty()) params.key + 1 else null
                callback.onResult(response, key)
            } catch (e: Exception) {
                updateState(LoadingState.Error(e.message))
            }
            updateState(LoadingState.HidePageLoading)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {}

    private fun updateState(state: LoadingState) {
        getState().postValue(state)
    }

    fun getState() = _state

    override fun invalidate() {
        super.invalidate()
        scope.cancel()
    }
}
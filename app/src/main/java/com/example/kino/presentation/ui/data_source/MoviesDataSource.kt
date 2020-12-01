package com.example.kino.presentation.ui.data_source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.kino.data.mapper.DataSource
import com.example.kino.data.model.movie.MoviesType
import com.example.kino.domain.model.Movie
import com.example.kino.domain.use_case.MoviesListsUseCase
import com.example.kino.presentation.model.GenresList
import com.example.kino.presentation.ui.MovieState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MoviesDataSource(
    private val scope: CoroutineScope,
    private val context: Context,
    private val moviesListsUseCase: MoviesListsUseCase,
    private val sessionId: String,
    private val movieType: MoviesType
) : PageKeyedDataSource<Int, Movie>() {

    companion object {
        const val PAGE_SIZE = 20
        private const val FIRST_PAGE = 1
    }

    init {
        GenresList.getGenres()
    }

    private val _state = MutableLiveData<MovieState>()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        updateState(MovieState.ShowLoading)
        scope.launch {
            val response = moviesListsUseCase.getMovies(FIRST_PAGE, sessionId, movieType)
            val movies = response.first ?: emptyList()
            val remote = response.second == DataSource.REMOTE
            for (movie in movies) {
                GenresList.setMovieGenres(movie, context)
            }
            val key = if (movies.isNotEmpty() && remote) FIRST_PAGE + 1 else null
            callback.onResult(movies, null, key)
            updateState(MovieState.HideLoading)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {}

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        updateState(MovieState.ShowLoading)
        scope.launch {
            if (params.key != FIRST_PAGE) updateState(MovieState.ShowPageLoading)
            else updateState(MovieState.HideLoading)

            val response = moviesListsUseCase.getMovies(params.key, sessionId, movieType)
            val movies = response.first ?: emptyList()
            val remote = response.second == DataSource.REMOTE
            for (movie in movies) {
                GenresList.setMovieGenres(movie, context)
            }
            val key = if (movies.isNotEmpty() && remote) params.key + 1 else null
            callback.onResult(movies, key)
            updateState(MovieState.HideLoading)
        }
    }

    private fun updateState(state: MovieState) {
        getState().postValue(state)
    }

    fun getState() = _state

    override fun invalidate() {
        super.invalidate()
        scope.cancel()
    }
}
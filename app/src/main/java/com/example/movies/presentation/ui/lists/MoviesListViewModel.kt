package com.example.movies.presentation.ui.lists

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.movies.core.base.BaseViewModel
import com.example.movies.data.model.movie.FavouriteMovie
import com.example.movies.data.model.movie.WatchListMovie
import com.example.movies.domain.model.DataSource
import com.example.movies.domain.model.Movie
import com.example.movies.domain.model.MoviesAnswer
import com.example.movies.domain.model.MoviesType
import com.example.movies.domain.model.MoviesType.*
import com.example.movies.domain.use_case.*
import com.example.movies.presentation.model.GenresList
import com.example.movies.presentation.ui.MovieState
import com.example.movies.presentation.ui.data_source.SearchDataSource
import com.example.movies.presentation.ui.data_source.SearchMoviesDataSourceFactory
import com.example.movies.presentation.utils.constants.MEDIA_TYPE
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.launch

class MoviesListViewModel(
    private val context: Context,
    sessionIdUseCase: SessionIdUseCase,
    private val likesUseCase: LikesUseCase,
    private val moviesListsUseCase: MoviesListsUseCase,
    private val localMoviesUseCase: LocalMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
) : BaseViewModel() {

    private val pagedListConfig: PagedList.Config

    private lateinit var searchMoviesDataSourceFactory: SearchMoviesDataSourceFactory
    lateinit var searchPagedList: LiveData<PagedList<Movie>>
    lateinit var searchStateLiveData: LiveData<MovieState>

    private val sessionId = sessionIdUseCase.getLocalSessionId()
    val liveData = MutableLiveData<State>()

    init {
        GenresList.getGenres()
        pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(SearchDataSource.PAGE_SIZE)
            .setPrefetchDistance(10)
            .build()
    }

    fun getMovies(type: MoviesType, page: Int = 1) {
        uiScope.launch {
            if (page == 1) liveData.value = State.ShowLoading
            likesUseCase.synchronizeLocalLikes(sessionId)
            val response = moviesListsUseCase.getMovies(page, type, sessionId)
            when (type) {
                TOP -> processLists(response, type)
                CURRENT -> processLists(response, type)
                UPCOMING -> processLists(response, type)
                POPULAR -> processLists(response, type)
                RATED -> processLists(response, type)
                FAVOURITES -> getFavouriteMovies(response, type)
                WATCH_LIST -> getWatchListMovies(response, type)
            }
        }
    }

    private fun getFavouriteMovies(response: MoviesAnswer, type: MoviesType) {
        processUserLists(response, type) {
            if (!response.movies.isNullOrEmpty()) {
                for (movie in response.movies) {
                    GenresList.setMovieGenres(context, movie)
                    movie.isFavourite = true
                }
            }
        }
    }

    private fun getWatchListMovies(response: MoviesAnswer, type: MoviesType) {
        processUserLists(response, type) {
            if (!response.movies.isNullOrEmpty()) {
                for (movie in response.movies) {
                    GenresList.setMovieGenres(context, movie)
                    movie.isInWatchList = true
                }
            }
        }
    }

    private fun processLists(response: MoviesAnswer, type: MoviesType) {
        if (!response.movies.isNullOrEmpty()) {
            for (movie in response.movies) {
                GenresList.setMovieGenres(context, movie)
                //getMovieStatuses(movie)
            }
        }
        if (response.page == 1) liveData.value = State.HideLoading
        liveData.value =
            State.Result(
                moviesList = response.movies ?: emptyList(),
                dataSource = response.dataSource,
                type = type,
                totalPages = response.totalPages
            )
    }

    private fun processUserLists(response: MoviesAnswer, type: MoviesType, block: () -> Unit) {
        block()
        if (response.page == 1) liveData.value = State.HideLoading
        liveData.value =
            State.Result(
                moviesList = response.movies ?: emptyList(),
                dataSource = response.dataSource,
                type = type,
                totalPages = response.totalPages
            )
    }

    fun searchMovies(query: String) {
        searchMoviesDataSourceFactory =
            SearchMoviesDataSourceFactory(uiScope, searchMoviesUseCase, query, context)
        searchPagedList =
            LivePagedListBuilder(searchMoviesDataSourceFactory, pagedListConfig).build()

        searchStateLiveData = Transformations.switchMap(
            searchMoviesDataSourceFactory.liveData,
            SearchDataSource::getState
        )
    }

    private fun updateIsFavourite(movie: FavouriteMovie) {
        uiScope.launch {
            likesUseCase.updateIsFavourite(movie, sessionId)
        }
    }

    private fun updateIsInWatchList(movie: WatchListMovie) {
        uiScope.launch {
            likesUseCase.updateIsInWatchList(movie, sessionId)
        }
    }

    fun addToFavourites(item: Movie) {
        if (item.id == null) return
        item.isFavourite = !item.isFavourite
        val selectedMovie = FavouriteMovie(MEDIA_TYPE, item.id, item.isFavourite)
        updateIsFavourite(selectedMovie)
    }

    fun addToWatchlist(item: Movie) {
        if (item.id == null) return
        item.isInWatchList = !item.isInWatchList
        val selectedMovie = WatchListMovie(MEDIA_TYPE, item.id, item.isInWatchList)
        updateIsInWatchList(selectedMovie)
    }

    fun getMovieStatuses(movie: Movie) {
        if (movie.id == null) return
        uiScope.launch {
            val movieStatus = likesUseCase.getMovieStates(movie.id, sessionId)
            if (movieStatus != null) {
                movie.isFavourite = movieStatus.favourite
                movie.isInWatchList = movieStatus.watchlist
                if (movieStatus.rated != false) {
                    val rating = movieStatus.rated as LinkedTreeMap<String, Double>
                    movie.rating = rating["value"]
                }
                localMoviesUseCase.updateLocalMovieIsFavourite(movie.isFavourite, movie.id)
                //localMoviesUseCase.updateLocalMovieIsInWatchList(movie.isInWatchList, movie.id)
                liveData.value = State.Update
            }
        }
    }

    sealed class State {
        object Update : State()
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(
            val moviesList: List<Movie>,
            val dataSource: DataSource? = null,
            val type: MoviesType? = null,
            val totalPages: Int = 1
        ) : State()
    }
}

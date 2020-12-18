package com.example.movies.presentation.ui.lists

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.movies.data.mapper.DataSource
import com.example.movies.data.model.movie.FavouriteMovie
import com.example.movies.data.model.movie.MoviesType
import com.example.movies.data.model.movie.WatchListMovie
import com.example.movies.domain.model.Movie
import com.example.movies.domain.use_case.*
import com.example.movies.presentation.BaseViewModel
import com.example.movies.presentation.model.GenresList
import com.example.movies.presentation.ui.MovieState
import com.example.movies.presentation.ui.data_source.SearchDataSource
import com.example.movies.presentation.ui.data_source.SearchMoviesDataSourceFactory
import com.example.movies.presentation.utils.constants.MEDIA_TYPE
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

    private val sessionId = sessionIdUseCase.getLocalSessionId(context)
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
            when (type) {
                MoviesType.TOP -> getTopMovies(page)
                MoviesType.CURRENT -> getCurrentPlaying(page)
                MoviesType.FAVOURITES -> getFavouriteMovies(page)
                MoviesType.POPULAR -> getPopularMovies(page)
                MoviesType.UPCOMING -> getUpcomingMovies(page)
                MoviesType.WATCH_LIST -> getWatchListMovies(page)
            }
        }
    }

    private suspend fun getTopMovies(page: Int) {

        val response = moviesListsUseCase.getTopMovies(page)
        if (!response.movies.isNullOrEmpty()) {
            for (movie in response.movies) {
                GenresList.setMovieGenres(movie, context)
                getMovieStatuses(movie)
            }
        }
        liveData.value = State.HideLoading
        liveData.value =
            State.Result(
                moviesList = response.movies ?: emptyList(),
                dataSource = response.dataSource,
                type = MoviesType.TOP,
                totalPages = response.totalPages
            )
    }

    private suspend fun getCurrentPlaying(page: Int) {
        val response = moviesListsUseCase.getCurrentPlaying(page)
        if (!response.movies.isNullOrEmpty()) {
            for (movie in response.movies) {
                GenresList.setMovieGenres(movie, context)
                getMovieStatuses(movie)
            }
        }
        liveData.value = State.HideLoading
        liveData.value =
            State.Result(
                response.movies ?: emptyList(),
                response.dataSource,
                MoviesType.CURRENT,
                response.totalPages
            )
    }

    private suspend fun getUpcomingMovies(page: Int) {
        val response = moviesListsUseCase.getUpcomingMovies(page)
        if (!response.movies.isNullOrEmpty()) {
            for (movie in response.movies) {
                GenresList.setMovieGenres(movie, context)
                getMovieStatuses(movie)
            }
        }
        liveData.value = State.HideLoading
        liveData.value =
            State.Result(
                response.movies ?: emptyList(),
                response.dataSource,
                MoviesType.UPCOMING,
                response.totalPages
            )
    }

    private suspend fun getPopularMovies(page: Int) {
        val response = moviesListsUseCase.getPopularMovies(page)
        if (!response.movies.isNullOrEmpty()) {
            for (movie in response.movies) {
                GenresList.setMovieGenres(movie, context)
                getMovieStatuses(movie)
            }
        }
        liveData.value = State.HideLoading
        liveData.value =
            State.Result(
                response.movies ?: emptyList(),
                response.dataSource,
                MoviesType.POPULAR,
                response.totalPages
            )
    }

    private suspend fun getFavouriteMovies(page: Int) {
        val response = moviesListsUseCase.getFavouriteMovies(sessionId, page)
        if (!response.movies.isNullOrEmpty()) {
            for (movie in response.movies) {
                GenresList.setMovieGenres(movie, context)
                movie.isFavourite = true
            }
        }
        liveData.value = State.HideLoading
        liveData.value = State.Result(
            response.movies ?: emptyList(),
            response.dataSource,
            MoviesType.FAVOURITES,
            response.totalPages
        )
    }

    private suspend fun getWatchListMovies(page: Int) {
        val response = moviesListsUseCase.getWatchListMovies(sessionId, page)
        if (!response.movies.isNullOrEmpty()) {
            for (movie in response.movies) {
                GenresList.setMovieGenres(movie, context)
                movie.isInWatchList = true
            }
        }
        liveData.value = State.HideLoading
        liveData.value = State.Result(
            response.movies ?: emptyList(),
            response.dataSource,
            MoviesType.WATCH_LIST,
            response.totalPages
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
        item.isFavourite = !item.isFavourite
        val selectedMovie = FavouriteMovie(MEDIA_TYPE, item.id, item.isFavourite)
        updateIsFavourite(selectedMovie)
    }

    fun addToWatchlist(item: Movie) {
        item.isInWatchList = !item.isInWatchList
        val selectedMovie = WatchListMovie(MEDIA_TYPE, item.id, item.isInWatchList)
        updateIsInWatchList(selectedMovie)
    }

    private fun getMovieStatuses(movie: Movie) {
        uiScope.launch {
            val movieStatus = likesUseCase.getMovieStates(movie.id, sessionId)
            if (movieStatus != null) {
                movie.isFavourite = movieStatus.favourite
                movie.isInWatchList = movieStatus.watchlist
                localMoviesUseCase.updateLocalMovieIsFavourite(movie.isFavourite, movie.id)
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

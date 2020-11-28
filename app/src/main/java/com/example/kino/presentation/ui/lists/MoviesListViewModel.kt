package com.example.kino.presentation.ui.lists

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.kino.data.mapper.DataSource
import com.example.kino.data.model.movie.FavouriteMovie
import com.example.kino.data.model.movie.MoviesType
import com.example.kino.data.model.movie.WatchListMovie
import com.example.kino.domain.model.Movie
import com.example.kino.domain.use_case.*
import com.example.kino.presentation.BaseViewModel
import com.example.kino.presentation.model.GenresList
import com.example.kino.presentation.ui.MovieState
import com.example.kino.presentation.ui.data_source.MoviesDataSourceFactory
import com.example.kino.presentation.ui.data_source.SearchDataSource
import com.example.kino.presentation.utils.constants.MEDIA_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoviesListViewModel(
    private val context: Context,
    private val sessionIdUseCase: SessionIdUseCase,
    private val likesUseCase: LikesUseCase,
    private val moviesListsUseCase: MoviesListsUseCase,
    private val localMoviesUseCase: LocalMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
) : BaseViewModel() {

    private val pagedListConfig: PagedList.Config

    private lateinit var searchMoviesDataSourceFactory: MoviesDataSourceFactory
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
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            likesUseCase.synchronizeLocalLikes(sessionId)
            when (type) {
                MoviesType.TOP -> getTopMovies(page)
                MoviesType.CURRENT -> getCurrentPlaying(page)
                MoviesType.FAVOURITES -> getFavouriteMovies()
                MoviesType.UPCOMING -> getUpcomingMovies(page)
                MoviesType.WATCH_LIST -> getWatchListMovies()
            }
        }
    }

    private suspend fun getTopMovies(page: Int) {
        val movies = moviesListsUseCase.getTopMovies(page)
        if (!movies.first.isNullOrEmpty()) {
            for (movie in movies.first!!) {
                GenresList.setMovieGenres(movie, context)
                getMovieStatuses(movie)
            }
        }
        liveData.value = State.HideLoading
        liveData.value = State.Result(movies.first, movies.second, MoviesType.TOP)
    }

    private suspend fun getCurrentPlaying(page: Int) {
        val movies = moviesListsUseCase.getCurrentPlaying(page)
        if (!movies.first.isNullOrEmpty()) {
            for (movie in movies.first!!) {
                GenresList.setMovieGenres(movie, context)
                getMovieStatuses(movie)
            }
        }
        liveData.value = State.HideLoading
        liveData.value = State.Result(movies.first, movies.second, MoviesType.CURRENT)
    }

    private suspend fun getUpcomingMovies(page: Int) {
        val movies = moviesListsUseCase.getUpcomingMovies(page)
        if (!movies.first.isNullOrEmpty()) {
            for (movie in movies.first!!) {
                GenresList.setMovieGenres(movie, context)
                getMovieStatuses(movie)
            }
        }
        liveData.value = State.HideLoading
        liveData.value = State.Result(movies.first, movies.second, MoviesType.UPCOMING)
    }

    private suspend fun getFavouriteMovies() {
        val movies = moviesListsUseCase.getFavouriteMovies(sessionId)

        if (!movies.first.isNullOrEmpty()) {
            for (movie in movies.first!!) {
                GenresList.setMovieGenres(movie, context)
                movie.isFavourite = true
            }
        }
        liveData.value = State.HideLoading
        liveData.value = State.Result(movies.first)
    }

    private suspend fun getWatchListMovies() {
        val movies = moviesListsUseCase.getWatchListMovies(sessionId)

        if (!movies.first.isNullOrEmpty()) {
            for (movie in movies.first!!) {
                GenresList.setMovieGenres(movie, context)
                movie.isInWatchList = true
            }
        }
        liveData.value = State.HideLoading
        liveData.value = State.Result(movies.first)
    }

    fun searchMovies(query: String) {
        searchMoviesDataSourceFactory =
            MoviesDataSourceFactory(
                Dispatchers.IO,
                searchMoviesUseCase,
                query,
                context
            )
        searchPagedList =
            LivePagedListBuilder(searchMoviesDataSourceFactory, pagedListConfig).build()

        searchStateLiveData = Transformations.switchMap(
            searchMoviesDataSourceFactory.liveData,
            SearchDataSource::getState
        )
    }

    private fun updateIsFavourite(movie: FavouriteMovie) {
        launch {
            likesUseCase.updateIsFavourite(movie, sessionId)
        }
    }

    private fun updateIsInWatchList(movie: WatchListMovie) {
        launch {
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
        launch {
            try {
                val movieStatus = likesUseCase.getRemoteMovieStatuses(movie.id, sessionId)
                if (movieStatus != null) {
                    movie.isFavourite = movieStatus.favourite
                    movie.isInWatchList = movieStatus.watchlist
                    localMoviesUseCase.updateLocalMovieIsFavourite(movie.isFavourite, movie.id)
                    liveData.value = State.Update
                }
            } catch (e: Exception) {
            }
        }
    }

    sealed class State {
        object Update : State()
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(
            val moviesList: List<Movie>?,
            val dataSource: DataSource? = null,
            val type: MoviesType? = null
        ) : State()
    }
}

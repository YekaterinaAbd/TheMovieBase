package com.example.kino.presentation.ui.lists

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.kino.R
import com.example.kino.data.model.movie.SelectedMovie
import com.example.kino.domain.model.Movie
import com.example.kino.domain.use_case.LikesUseCase
import com.example.kino.domain.use_case.LocalMoviesUseCase
import com.example.kino.domain.use_case.MoviesListsUseCase
import com.example.kino.domain.use_case.SessionIdUseCase
import com.example.kino.presentation.BaseViewModel
import com.example.kino.presentation.model.GenresList
import com.example.kino.presentation.ui.MovieState
import com.example.kino.presentation.ui.data_source.MoviesDataSource
import com.example.kino.presentation.ui.data_source.MoviesDataSourceFactory
import com.example.kino.presentation.utils.constants.MEDIA_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MoviesListViewModel(
    private val context: Context,
    localSessionId: SessionIdUseCase,
    private val likes: LikesUseCase,
    private val moviesLists: MoviesListsUseCase,
    private val localMovies: LocalMoviesUseCase
) : BaseViewModel() {

    private val pagedListConfig: PagedList.Config

    private lateinit var searchMoviesDataSourceFactory: MoviesDataSourceFactory
    lateinit var searchPagedList: LiveData<PagedList<Movie>>
    lateinit var searchStateLiveData: LiveData<MovieState>

    private val sessionId = localSessionId.getLocalSessionId(context)
    val liveData = MutableLiveData<State>()

    init {
        GenresList.getGenres()
        pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(MoviesDataSource.PAGE_SIZE)
            .setPrefetchDistance(10)
            .build()
    }

    fun getMovies(type: MoviesType, page: Int = 1) {
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            likes.loadLocalLikes(sessionId)
            when (type) {
                MoviesType.TOP -> getTopMovies(page)
                MoviesType.CURRENT_PLAYING -> getCurrentPlaying(page)
                MoviesType.FAVOURITES -> getFavouriteMovies()
                MoviesType.UPCOMING -> getUpcomingMovies(page)
            }
        }
    }

    private suspend fun getTopMovies(page: Int): Pair<List<Movie>?, Boolean>? {
        val movies = moviesLists.getTopMovies(page)
        if (movies.second && !movies.first.isNullOrEmpty()) {
            for (movie in movies.first!!) {
                setMovieGenres(movie)
                saveLikeStatus(movie)
            }
            if (page == 1) {
                localMovies.deleteLocalMovies()
                localMovies.insertLocalMovies(movies.first!!)
            }
        }
        liveData.value = State.HideLoading
        liveData.value = State.Result(movies.first, !movies.second, MoviesType.TOP)
        return movies
    }

    private suspend fun getCurrentPlaying(page: Int): Pair<List<Movie>?, Boolean>? {
        val movies = moviesLists.getCurrentPlaying(page)
        if (movies.second && !movies.first.isNullOrEmpty()) {
            for (movie in movies.first!!) {
                setMovieGenres(movie)
                saveLikeStatus(movie)
            }
            if (page == 1) {
                localMovies.deleteLocalMovies()
                localMovies.insertLocalMovies(movies.first!!)
            }
        }
        liveData.value = State.HideLoading
        liveData.value = State.Result(movies.first, !movies.second, MoviesType.CURRENT_PLAYING)
        return movies
    }

    private suspend fun getUpcomingMovies(page: Int): Pair<List<Movie>?, Boolean>? {
        val movies = moviesLists.getUpcomingMovies(page)
        if (movies.second && !movies.first.isNullOrEmpty()) {
            for (movie in movies.first!!) {
                setMovieGenres(movie)
                saveLikeStatus(movie)
            }
            if (page == 1) {
                localMovies.deleteLocalMovies()
                localMovies.insertLocalMovies(movies.first!!)
            }
        }
        liveData.value = State.HideLoading
        liveData.value = State.Result(movies.first, !movies.second, MoviesType.UPCOMING)
        return movies
    }

    private suspend fun getFavouriteMovies(): Pair<List<Movie>?, Boolean>? {
        val movies = moviesLists.getFavouriteMovies(sessionId)

        if (movies.second && !movies.first.isNullOrEmpty()) {
            for (movie in movies.first!!) {
                setMovieGenres(movie)
                movie.isClicked = true
            }
        }
        liveData.value = State.HideLoading
        liveData.value = State.Result(movies.first, !movies.second)
        return movies
    }

    fun searchMovies(query: String) {
        searchMoviesDataSourceFactory = MoviesDataSourceFactory(moviesLists, Dispatchers.IO, query)
        searchPagedList =
            LivePagedListBuilder(searchMoviesDataSourceFactory, pagedListConfig).build()

        searchStateLiveData = Transformations.switchMap(
            searchMoviesDataSourceFactory.liveData,
            MoviesDataSource::getState
        )
    }

    private fun updateLikeStatus(movie: SelectedMovie) {
        launch {
            likes.updateLikeStatus(movie, sessionId)
        }
    }

    private fun setMovieGenres(movie: Movie) {
        movie.genreNames = ""
        movie.genreIds?.forEach { genreId ->
            val genreName = GenresList.genres?.get(genreId)
                .toString().toLowerCase(Locale.ROOT)
            movie.genreNames += context.getString(R.string.genre_name, genreName)
        }
        if (movie.genreNames.length >= 2)
            movie.genreNames = movie.genreNames.substring(0, movie.genreNames.length - 2)
    }

    fun addToFavourites(item: Movie) {
        item.isClicked = !item.isClicked
        val selectedMovie = SelectedMovie(MEDIA_TYPE, item.id, item.isClicked)
        updateLikeStatus(selectedMovie)
    }

    private fun getLikeStatus(movie: Movie) {
        launch {
            val movieStatus = likes.getRemoteMovieLike(movie.id, sessionId)
            if (movieStatus != null) {
                movie.isClicked = movieStatus
                liveData.value = State.Update
            }
        }
    }

    private fun saveLikeStatus(movie: Movie) {
        launch {
            try {
                val movieStatus = likes.getRemoteMovieLike(movie.id, sessionId)
                if (movieStatus != null) {
                    movie.isClicked = movieStatus
                    localMovies.updateLocalMovieIsLiked(movie.isClicked, movie.id)
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
            val isLocal: Boolean = false,
            val type: MoviesType? = null
        ) : State()
    }
}

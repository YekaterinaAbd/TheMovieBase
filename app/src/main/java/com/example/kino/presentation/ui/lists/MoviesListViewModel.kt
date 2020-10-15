package com.example.kino.presentation.ui.lists

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.kino.R
import com.example.kino.data.model.movie.SelectedMovie
import com.example.kino.domain.model.Movie
import com.example.kino.domain.use_case.LikesUseCase
import com.example.kino.domain.use_case.LocalMoviesUseCase
import com.example.kino.domain.use_case.MoviesListsUseCase
import com.example.kino.domain.use_case.SessionIdUseCase
import com.example.kino.presentation.BaseViewModel
import com.example.kino.presentation.model.GenresList
import com.example.kino.presentation.utils.constants.MEDIA_TYPE
import kotlinx.coroutines.launch
import java.util.*

class MoviesListViewModel(
    private val context: Context,
    private val localSessionId: SessionIdUseCase,
    private val likes: LikesUseCase,
    private val moviesLists: MoviesListsUseCase,
    private val localMovies: LocalMoviesUseCase
) : BaseViewModel() {

    private val sessionId = localSessionId.getLocalSessionId(context)
    val liveData = MutableLiveData<State>()

    init {
        GenresList.getGenres()
    }

    fun getMovies(type: FragmentEnum, page: Int = 1) {
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            likes.loadLocalLikes(sessionId)
            when (type) {
                FragmentEnum.TOP -> getTopMovies(page)
                FragmentEnum.FAVOURITES -> getFavouriteMovies()
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
        liveData.value = State.Result(movies.first, !movies.second)
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

    fun searchMovies(query: String, page: Int = 1) {
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            val movies = moviesLists.searchMovies(query, page)
            if (!movies.isNullOrEmpty()) {
                for (movie in movies) {
                    setMovieGenres(movie)
                    getLikeStatus(movie)
                }
            }
            liveData.value = State.Result(movies)
            liveData.value = State.HideLoading
        }
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
        data class Result(val moviesList: List<Movie>?, val isLocal: Boolean = false) :
            State()
    }
}

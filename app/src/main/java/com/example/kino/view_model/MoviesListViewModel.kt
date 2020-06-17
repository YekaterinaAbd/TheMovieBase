package com.example.kino.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.kino.R
import com.example.kino.model.movie.GenresList
import com.example.kino.model.movie.Movie
import com.example.kino.model.movie.MovieStatus
import com.example.kino.model.movie.SelectedMovie
import com.example.kino.model.repository.MovieRepository
import com.example.kino.utils.FragmentEnum
import com.example.kino.utils.constants.API_KEY
import com.example.kino.utils.constants.MEDIA_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MoviesListViewModel(
    private val context: Context,
    private var movieRepository: MovieRepository
) : BaseViewModel() {

    private val sessionId = movieRepository.getLocalSessionId(context)
    val liveData = MutableLiveData<State>()

    init {
        GenresList.getGenres()
    }


    fun getMovies(type: FragmentEnum, page: Int = 1) {
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            val moviesList = withContext(Dispatchers.IO) {
                try {
                    updateFavouritesDB()
                    when (type) {
                        FragmentEnum.TOP -> return@withContext getTopMovies(page)
                        FragmentEnum.FAVOURITES -> return@withContext getFavouriteMovies()
                    }
                } catch (e: Exception) {
                    when (type) {
                        FragmentEnum.TOP -> return@withContext movieRepository.getLocalMovies()
                        FragmentEnum.FAVOURITES -> return@withContext movieRepository.getLocalFavouriteMovies()
                    }
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(moviesList, isLocal)
        }
    }

    private fun updateFavouritesDB() {
        val moviesToUpdate = movieRepository.getLocalMovieStatuses()
        if (!moviesToUpdate.isNullOrEmpty()) {
            for (movie in moviesToUpdate) {
                updateLikeStatus(SelectedMovie(MEDIA_TYPE, movie.movieId, movie.selectedStatus))
            }
        }
        movieRepository.deleteLocalMovieStatuses()
    }

    private var isLocal = false
    private suspend fun getTopMovies(page: Int): List<Movie>? {
        return try {
            val movies = movieRepository.getRemoteMovieList(API_KEY, page)
            if (!movies.isNullOrEmpty()) {
                isLocal = false
                for (movie in movies) {
                    setMovieGenres(movie)
                    saveLikeStatus(movie)
                }
                if (page == 1) {
                    movieRepository.deleteLocalMovies()
                    movieRepository.insertLocalMovies(movies)
                }
            }
            movies
        } catch (e: Exception) {
            isLocal = true
            movieRepository.getLocalMovies()
        }
    }

    private suspend fun getFavouriteMovies(): List<Movie>? {
        return try {
            val movies = movieRepository.getRemoteFavouriteMovies(API_KEY, sessionId)

            if (!movies.isNullOrEmpty()) {
                for (movie in movies) {
                    setMovieGenres(movie)
                    movie.isClicked = true
                }
            }
            movies
        } catch (e: Exception) {
            movieRepository.getLocalFavouriteMovies()
        }
    }

    private fun setMovieGenres(movie: Movie) {
        movie.genreNames = ""
        movie.genreIds?.forEach { genreId ->
            val genreName = GenresList.genres?.get(genreId)
                .toString().toLowerCase(Locale.ROOT)
            movie.genreNames += context.getString(R.string.genre_name, genreName)
        }
        movie.genreNames = movie.genreNames.substring(0, movie.genreNames.length - 2)
    }

    fun addToFavourites(item: Movie) {
        item.isClicked = !item.isClicked
        val selectedMovie = SelectedMovie(MEDIA_TYPE, item.id, item.isClicked)
        updateLikeStatus(selectedMovie)
    }

    private fun updateLikeStatus(movie: SelectedMovie) {
        launch {
            withContext(Dispatchers.IO) {
                try {
                    movieRepository.updateRemoteFavourites(API_KEY, sessionId, movie)
                    movieRepository.updateLocalMovieIsCLicked(movie.selectedStatus, movie.movieId)

                } catch (e: Exception) {
                    movieRepository.updateLocalMovieIsCLicked(movie.selectedStatus, movie.movieId)
                    movieRepository.insertLocalMovieStatus(
                        MovieStatus(movie.movieId, movie.selectedStatus)
                    )
                    Log.d("testt", movieRepository.getLocalMovieStatuses().toString())
                }
            }
        }
    }

    private fun saveLikeStatus(movie: Movie) {
        launch {
            try {
                val movieStatus = movieRepository.getRemoteMovieStates(
                    movie.id,
                    API_KEY, sessionId
                )
                if (movieStatus != null) {
                    movie.isClicked = movieStatus
                    withContext(Dispatchers.IO) {
                        movieRepository.updateLocalMovieIsCLicked(movie.isClicked, movie.id)
                    }
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
        data class Result(val moviesList: List<Movie>?, val isLocal: Boolean) : State()
    }
}

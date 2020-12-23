package com.example.movies.presentation.model

import android.content.Context
import com.example.movies.R
import com.example.movies.data.network.API_KEY
import com.example.movies.domain.model.Movie
import com.example.movies.domain.repository.MovieRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import kotlin.coroutines.CoroutineContext

object GenresList : CoroutineScope, KoinComponent {
    var genres: MutableMap<Int, String> = mutableMapOf()
    private var job = Job()
    private val movieRepository: MovieRepository by inject()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun getGenres() {
        launch {
            try {
                val genresList = movieRepository.getRemoteGenres(API_KEY) ?: return@launch
                val genresBunch = genresList.genres ?: return@launch
                for (genre in genresBunch) {
                    if (genre.genreId == null || genre.genre == null) continue
                    genres[genre.genreId] = genre.genre
                }

            } catch (e: Exception) {
            }
        }
    }

    fun setMovieGenres(context: Context, movie: Movie) {
        movie.genreNames = ""
        movie.genreIds?.forEach { genreId ->
            val genreName = genres[genreId]
                .toString().toLowerCase(Locale.ROOT)
            movie.genreNames += context.getString(R.string.genre_name, genreName)
        }
        if (movie.genreNames.length >= 2)
            movie.genreNames = movie.genreNames.substring(0, movie.genreNames.length - 2)
    }
}

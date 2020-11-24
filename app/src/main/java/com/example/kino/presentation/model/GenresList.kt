package com.example.kino.presentation.model

import android.content.Context
import com.example.kino.R
import com.example.kino.data.network.API_KEY
import com.example.kino.domain.model.Movie
import com.example.kino.domain.repository.MovieRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext

object GenresList : CoroutineScope, KoinComponent {
    var genres: MutableMap<Int, String>? = HashMap()
    private var job = Job()
    private val movieRepository: MovieRepository by inject()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun getGenres() {
        launch {
            try {
                val genresList = movieRepository.getRemoteGenres(API_KEY)
                if (genresList != null) {
                    val genresBunch = genresList.genres
                    for (genre in genresBunch) {
                        genres?.set(genre.genreId, genre.genre)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun setMovieGenres(movie: Movie, context: Context) {
        movie.genreNames = ""
        movie.genreIds?.forEach { genreId ->
            val genreName = GenresList.genres?.get(genreId)
                .toString().toLowerCase(Locale.ROOT)
            movie.genreNames += context.getString(R.string.genre_name, genreName)
        }
        if (movie.genreNames.length >= 2)
            movie.genreNames = movie.genreNames.substring(0, movie.genreNames.length - 2)
    }
}

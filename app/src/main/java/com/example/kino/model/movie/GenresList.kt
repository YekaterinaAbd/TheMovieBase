package com.example.kino.model.movie

import com.example.kino.model.repository.MovieRepository
import com.example.kino.utils.constants.API_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
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
}
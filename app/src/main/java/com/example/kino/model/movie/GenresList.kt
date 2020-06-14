package com.example.kino.model.movie

import com.example.kino.model.repository.MovieRepository
import com.example.kino.model.repository.MovieRepositoryImpl
import com.example.kino.utils.RetrofitService
import com.example.kino.utils.constants.API_KEY
import com.example.kino.utils.constants.NULL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object GenresList : CoroutineScope {
    var genres: MutableMap<Int, String>? = HashMap()
    private var job = Job()
    private var movieRepository: MovieRepository =
        MovieRepositoryImpl(NULL, RetrofitService.getPostApi())
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
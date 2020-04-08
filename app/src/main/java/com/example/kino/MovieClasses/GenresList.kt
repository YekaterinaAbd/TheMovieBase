package com.example.kino.MovieClasses

import com.example.kino.ApiKey
import com.example.kino.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object GenresList : CoroutineScope {
    var genres: MutableMap<Int, String>? = HashMap()
    private var job = Job()


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    fun getGenres() {
        launch {
            val response = RetrofitService.getPostApi().getGenres(ApiKey)
            if (response.isSuccessful) {
                val receivedGenres = response.body()
                if (receivedGenres != null) {
                    val genresBunch = receivedGenres.genres
                    for (genre in genresBunch) {
                        genres?.set(genre.genreId, genre.genre)
                    }
                }
            }
        }
    }
}
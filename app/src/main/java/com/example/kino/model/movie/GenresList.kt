package com.example.kino.model.movie

import com.example.kino.utils.RetrofitService
import com.example.kino.utils.API_KEY
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
            try {
                val response = RetrofitService.getPostApi().getGenres(API_KEY)
                if (response.isSuccessful) {
                    val receivedGenres = response.body()
                    if (receivedGenres != null) {
                        val genresBunch = receivedGenres.genres
                        for (genre in genresBunch) {
                            genres?.set(genre.genreId, genre.genre)
                        }
                    }
                }
            } catch (e: Exception) {

            }

        }
    }
}
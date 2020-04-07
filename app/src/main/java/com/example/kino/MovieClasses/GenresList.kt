package com.example.kino.MovieClasses

import com.example.kino.ApiKey
import com.example.kino.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GenresList {
    var genres: MutableMap<Int, String>? = HashMap()

    fun getGenres() {
        RetrofitService.getPostApi().getGenres(ApiKey).enqueue(object : Callback<Genres> {
            override fun onFailure(call: Call<Genres>, t: Throwable) {}

            override fun onResponse(call: Call<Genres>, response: Response<Genres>) {
                if(response.isSuccessful){
                    val receivedGenres = response.body()
                    if (receivedGenres!= null) {
                        val genreResults = receivedGenres.genres
                        for (genreRes in genreResults){
                            genres?.set(genreRes.genreId, genreRes.genre)
                        }
                    }
                }
            }
        })
    }
}
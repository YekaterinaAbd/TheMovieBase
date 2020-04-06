package com.example.kino.MovieClasses

import com.example.kino.ApiKey
import com.example.kino.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GenresList {
    var genres: MutableMap<Int, String>? = HashMap()

    fun getGenres() {
        RetrofitService.getPostApi().getGenres(ApiKey).enqueue(object : Callback<GenreResults> {
            override fun onFailure(call: Call<GenreResults>, t: Throwable) {}

            override fun onResponse(call: Call<GenreResults>, response: Response<GenreResults>) {
                if(response.isSuccessful){
                    val ans = response.body()
                    if (ans != null) {
                        val genreResults = ans.genres
                        for (genreRes in genreResults){
                            genres?.set(genreRes.genreId, genreRes.genre)
                        }
                    }
                }
            }
        })
    }
}
package com.example.kino.MovieClasses

import com.example.kino.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GenresList {
    var genres: MutableMap<Int, String>? = HashMap()
    private val API_KEY: String = "d118a5a4e56930c8ce9bd2321609d877"

    fun getGenres() {
        RetrofitService.getPostApi().getGenres(API_KEY).enqueue(object : Callback<GenreResults> {
            override fun onFailure(call: Call<GenreResults>, t: Throwable) {}

            override fun onResponse(call: Call<GenreResults>, response: Response<GenreResults>) {
                if(response.isSuccessful){
                    val ans = response.body()
                    if (ans != null) {
                        val array = ans.genres
                        for (a in array){
                            genres?.set(a.genre_id, a.genre)
                        }
                    }
                }
            }
        })
    }
}
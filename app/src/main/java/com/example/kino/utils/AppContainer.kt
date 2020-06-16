package com.example.kino.utils

import android.content.Context
import com.example.kino.model.database.MovieDatabase
import com.example.kino.model.repository.AccountRepositoryImpl
import com.example.kino.model.repository.MarkerRepositoryImpl
import com.example.kino.model.repository.MovieRepositoryImpl
import com.example.kino.utils.constants.BASE_URL
import com.example.kino.view_model.MovieListViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {
    private val retrofit =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build().create(PostApi::class.java)
    private val movieDao = MovieDatabase.getDatabase(context).movieDao()
    private val movieStatusDao = MovieDatabase.getDatabase(context).movieStatusDao()
    private val markerDao = MovieDatabase.getDatabase(context).markerDao()

    val movieRepository = MovieRepositoryImpl(movieDao, retrofit, movieStatusDao)
    val accountRepository = AccountRepositoryImpl(retrofit)
    val markerRepository = MarkerRepositoryImpl(markerDao)

    val movieListViewModelFactory = MovieListViewModelFactory(context, movieRepository)
}
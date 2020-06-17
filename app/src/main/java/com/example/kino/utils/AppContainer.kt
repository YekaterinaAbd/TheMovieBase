package com.example.kino

import android.content.Context
import android.content.SharedPreferences
import com.example.kino.model.database.MarkerDao
import com.example.kino.model.database.MovieDao
import com.example.kino.model.database.MovieDatabase
import com.example.kino.model.database.MovieStatusDao
import com.example.kino.model.repository.AccountRepositoryImpl
import com.example.kino.model.repository.MarkerRepositoryImpl
import com.example.kino.model.repository.MovieRepositoryImpl
import com.example.kino.utils.PostApi
import com.example.kino.utils.constants.BASE_URL
import com.example.kino.view_model.MovieViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        //.client(RetrofitService.getOkHttp())
        .build()
        .create(PostApi::class.java)

    private val movieDao: MovieDao = MovieDatabase.getDatabase(context).movieDao()
    private val movieStatusDao: MovieStatusDao = MovieDatabase.getDatabase(context).movieStatusDao()
    private val markerDao: MarkerDao = MovieDatabase.getDatabase(context).markerDao()

    val movieRepository = MovieRepositoryImpl(movieDao, retrofit, movieStatusDao)
    val accountRepository = AccountRepositoryImpl(retrofit)
    private val markerRepository = MarkerRepositoryImpl(markerDao)

    val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file), Context.MODE_PRIVATE
    )

    val movieViewModelFactory = MovieViewModelFactory(context, movieRepository, markerRepository)
}

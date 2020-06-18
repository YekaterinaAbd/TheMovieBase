package com.example.kino.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.kino.R
import com.example.kino.model.database.MarkerDao
import com.example.kino.model.database.MovieDao
import com.example.kino.model.database.MovieDatabase
import com.example.kino.model.database.MovieStatusDao
import com.example.kino.model.repository.*
import com.example.kino.utils.constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer private constructor(context: Context) {

    companion object {
        private lateinit var INSTANCE: AppContainer
        private var initialized = false

        fun init(context: Context) {
            INSTANCE = AppContainer(context)
            initialized = true
        }

        fun getPreferences(): SharedPreferences = INSTANCE.sharedPreferences
        fun getMovieRepository(): MovieRepository = INSTANCE.movieRepository
        fun getAccountRepository(): AccountRepository = INSTANCE.accountRepository
        fun getMarkerRepository(): MarkerRepository = INSTANCE.markerRepository

    }

    private val retrofit: PostApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            //.client(RetrofitService.getOkHttp())
            .build()
            .create(PostApi::class.java)
    }

    private val movieDao: MovieDao = MovieDatabase.getDatabase(context).movieDao()
    private val movieStatusDao: MovieStatusDao = MovieDatabase.getDatabase(context).movieStatusDao()
    private val markerDao: MarkerDao = MovieDatabase.getDatabase(context).markerDao()

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(
            context.getString(R.string.preference_file), Context.MODE_PRIVATE
        )
    }

    private val movieRepository: MovieRepository by lazy {
        MovieRepositoryImpl(movieDao, retrofit, movieStatusDao, sharedPreferences)
    }
    private val accountRepository: AccountRepository by lazy {
        AccountRepositoryImpl(retrofit, sharedPreferences)
    }
    private val markerRepository: MarkerRepository by lazy {
        MarkerRepositoryImpl(markerDao)
    }
}

package com.example.kino.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.kino.R
import com.example.kino.model.database.MarkerDao
import com.example.kino.model.database.MovieDao
import com.example.kino.model.database.MovieDatabase
import com.example.kino.model.database.MovieStatusDao
import com.example.kino.model.repository.*
import com.example.kino.utils.constants.BASE_URL
import com.example.kino.view_model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { createLoggingInterceptor() }
    single { createHttpClient(httpLoggingInterceptor = get()) }
    single { createApiService(okHttpClient = get()) }
}
val storageModule = module {
    single { getSharedPreferences(context = get()) }
    single { getMovieDao(context = get()) }
    single { getMovieStatusDao(context = get()) }
    single { getMarkerDao(context = get()) }
}
val repositoryModule = module {
    single<MovieRepository> {
        MovieRepositoryImpl(
            movieDao = get(),
            service = get(),
            movieStatusDao = get(),
            sharedPreferences = get()
        )
    }
    single<AccountRepository> { AccountRepositoryImpl(service = get(), sharedPreferences = get()) }
    single<MarkerRepository> { MarkerRepositoryImpl(markerDao = get()) }
}

val viewModelModule = module {
    viewModel { MoviesListViewModel(context = get(), movieRepository = get()) }
    viewModel { MovieDetailsViewModel(context = get(), movieRepository = get()) }
    viewModel { AccountViewModel(context = get(), accountRepository = get()) }
    viewModel { MarkersViewModel(markerRepository = get()) }
    viewModel { SignInViewModel(context = get(), accountRepository = get()) }
    viewModel { ThemeViewModel(context = get(), accountRepository = get()) }
}
val appModule = listOf(networkModule, storageModule, repositoryModule, viewModelModule)

private fun getMovieDao(context: Context): MovieDao {
    return MovieDatabase.getDatabase(context).movieDao()
}

private fun getMovieStatusDao(context: Context): MovieStatusDao {
    return MovieDatabase.getDatabase(context).movieStatusDao()
}

private fun getMarkerDao(context: Context): MarkerDao {
    return MovieDatabase.getDatabase(context).markerDao()
}

private fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(
        context.getString(R.string.preference_file),
        Context.MODE_PRIVATE
    )
}

private fun createApiService(
    okHttpClient: OkHttpClient
): PostApi {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(PostApi::class.java)
}

private fun createHttpClient(
    httpLoggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor)
    return okHttpClient.build()
}

private fun createLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor(logger = object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            Log.d("OkHttp", message)
        }
    }).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

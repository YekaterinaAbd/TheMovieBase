package com.example.kino.utils

import android.util.Log
import com.example.kino.model.account.LoginValidationData
import com.example.kino.model.account.Session
import com.example.kino.model.account.Token
import com.example.kino.model.movie.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

object RetrofitService {

    fun getPostApi(): PostApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttp())
            .build()
        return retrofit.create(PostApi::class.java)
    }

    private fun getOkHttp(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
        return okHttpClient.build()
    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(logger = object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("OkHttp", message)
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}

interface PostApi {

    @GET("movie/top_rated")
    suspend fun getMovieList(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<Movies>

    @GET("movie/{id}")
    suspend fun getMovieById(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): Response<Movie>

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavouriteMovies(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<Movies>

    @POST("account/{account_id}/favorite")
    suspend fun addRemoveFavourites(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body fav: SelectedMovie
    ): Response<StatusResponse>

    @GET("movie/{movie_id}/account_states")
    suspend fun getMovieStates(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<MovieStatus>

    @GET("genre/movie/list")
    suspend fun getGenres(@Query("api_key") apiKey: String): Response<Genres>

    @GET("authentication/token/new")
    suspend fun createRequestToken(@Query("api_key") apiKey: String): Response<Token>

    @POST("authentication/token/validate_with_login")
    suspend fun validateWithLogin(
        @Query("api_key") apiKey: String,
        @Body data: LoginValidationData
    ): Response<Token>

    @POST("authentication/session/new")
    suspend fun createSession(
        @Query("api_key") apiKey: String,
        @Body token: Token
    ): Response<Session>
}

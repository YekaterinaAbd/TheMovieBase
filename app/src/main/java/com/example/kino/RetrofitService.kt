package com.example.kino

import android.util.Log
import com.example.kino.AccountClasses.LoginValidationData
import com.example.kino.AccountClasses.SessionReciever
import com.example.kino.AccountClasses.Token
import com.example.kino.MovieClasses.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

object RetrofitService {

    private const val BASE_URL = "https://api.themoviedb.org/3/"

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
    fun getMovieList(@Query("api_key") apiKey: String): Call<Movies>

    @GET("movie/{id}")
    fun getMovieById(@Path("id")  id: Int,
                     @Query("api_key") apiKey: String): Call<MovieDetails>

    @GET("genre/movie/list")
    fun getGenres(@Query("api_key") apiKey: String): Call<Genres>

    @GET("authentication/token/new")
    fun createRequestToken(@Query("api_key") apiKey: String): Call<Token>

    @POST("authentication/token/validate_with_login")
    fun validateWithLogin(@Query("api_key") apiKey: String,
                          @Body data: LoginValidationData): Call<Token>

    @POST("authentication/session/new")
    fun createSession(@Query("api_key") apiKey: String,
                      @Body token: Token): Call<SessionReciever>

    @GET("account/{account_id}/favorite/movies")
    fun getFavouriteMovies(@Query("api_key") apiKey: String,
                           @Query("session_id") sessionId: String): Call<Movies>

    @POST("account/{account_id}/favorite")
    fun addRemoveFavourites(@Query("api_key") apiKey: String,
                            @Query("session_id") sessionId: String,
                            @Body fav: SelectedMovie): Call<StatusResponse>

    @GET("movie/{movie_id}/account_states")
    fun getMovieStates(@Path("movie_id") movieId: Int,
                       @Query("api_key") apiKey: String,
                       @Query("session_id") sessionId: String):Call<MovieStatus>
}


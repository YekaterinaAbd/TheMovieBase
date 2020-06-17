package com.example.kino.utils

import com.example.kino.model.account.LoginValidationData
import com.example.kino.model.account.Session
import com.example.kino.model.account.Token
import com.example.kino.model.movie.*
import retrofit2.Response
import retrofit2.http.*

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

package com.example.kino.data.network

import com.example.kino.data.model.account.LoginValidationData
import com.example.kino.data.model.account.Session
import com.example.kino.data.model.account.Success
import com.example.kino.data.model.account.Token
import com.example.kino.data.model.entities.MovieStatus
import com.example.kino.data.model.movie.*
import retrofit2.Response
import retrofit2.http.*

interface MovieApi {

    @GET("movie/top_rated")
    suspend fun getTopMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<Movies>

    @GET("movie/now_playing")
    suspend fun getCurrentMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<Movies>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<Movies>

    @GET("movie/{id}")
    suspend fun getMovieById(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): Response<RemoteMovieDetails>

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String
    ): Response<Movies>

    @GET("movie/{movie_id}/keywords")
    suspend fun getKeywords(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String
    ): Response<Keywords>

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<Videos>

    @GET("movie/{movie_id}/account_states")
    suspend fun getMovieStates(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<MovieStatus>

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavouriteMovies(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<Movies>

    @GET("account/{account_id}/watchlist/movies")
    suspend fun getMoviesWatchList(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<Movies>

    @POST("account/{account_id}/favorite")
    suspend fun markFavourite(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body fav: FavouriteMovie
    ): Response<StatusResponse>

    @POST("account/{account_id}/watchlist")
    suspend fun markWatchList(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body fav: WatchListMovie
    ): Response<StatusResponse>

    @GET("genre/movie/list")
    suspend fun getGenres(@Query("api_key") apiKey: String): Response<Genres>

    @GET("/3/search/movie")
    suspend fun searchMovies(
        @Query("api_key") key: String,
        @Query("query") query: String?,
        @Query("page") page: Int
    ): Response<Movies>

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

    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    suspend fun deleteSession(
        @Query("api_key") apiKey: String,
        @Body session: Session
    ): Response<Success>

}

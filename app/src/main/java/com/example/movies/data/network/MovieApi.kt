package com.example.movies.data.network

import com.example.movies.data.model.entities.MovieStatus
import com.example.movies.data.model.movie.*
import com.example.movies.data.model.movie.response.Movies
import com.example.movies.data.model.movie.response.StatusResponse
import com.google.gson.JsonObject
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

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<Movies>


    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavouriteMovies(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String?
    ): Response<Movies>

    @GET("account/{account_id}/watchlist/movies")
    suspend fun getMoviesWatchList(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String?
    ): Response<Movies>

    @GET("account/{account_id}/rated/movies")
    suspend fun getRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int
    ): Response<Movies>

    @GET("movie/{id}")
    suspend fun getMovieById(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): Response<MovieDetails>

    @GET("movie/{movie_id}/keywords")
    suspend fun getMovieKeywords(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String
    ): Response<Keywords>

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<Videos>

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<Credits>

    @GET("movie/{movie_id}/account_states")
    suspend fun getMovieStates(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<MovieStatus>

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String
    ): Response<Movies>

    @POST("account/{account_id}/favorite")
    suspend fun markMovieFavourite(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body fav: FavouriteMovie
    ): Response<StatusResponse>

    @POST("account/{account_id}/watchlist")
    suspend fun markMovieInWatchList(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body fav: WatchListMovie
    ): Response<StatusResponse>

    @POST("movie/{movie_id}/rating")
    suspend fun rateMovie(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body rating: JsonObject
    ): Response<JsonObject>

    @GET("genre/movie/list")
    suspend fun getGenres(@Query("api_key") apiKey: String): Response<Genres>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") key: String,
        @Query("query") query: String?,
        @Query("page") page: Int
    ): Response<Movies>

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") key: String,
        @Query("page") page: Int,
        @Query("with_genres") genres: String?,
        @Query("with_keywords") keywords: String?
    ): Response<Movies>
}

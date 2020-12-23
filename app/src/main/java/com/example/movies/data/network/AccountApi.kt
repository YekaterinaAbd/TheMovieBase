package com.example.movies.data.network

import com.example.movies.data.model.account.*
import retrofit2.Response
import retrofit2.http.*

interface AccountApi {

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

    @GET("account")
    suspend fun getAccountInfo(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<Account>
}
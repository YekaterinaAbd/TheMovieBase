package com.example.movies.domain.repository

import android.content.Context
import com.example.movies.data.model.account.LoginValidationData
import com.example.movies.data.model.account.Token

interface AccountRepository {
    suspend fun getSessionId(apiKey: String, token: Token): String?
    suspend fun createToken(apiKey: String): Token?
    suspend fun validateWithLogin(apiKey: String, data: LoginValidationData): Boolean
    suspend fun logOut(apiKey: String, context: Context): Boolean?

    fun getLocalUsername(context: Context): String
    fun getLocalPassword(context: Context): String
    fun hasSessionId(context: Context): Boolean
    fun saveLoginData(context: Context, username: String, password: String, sessionId: String)
    fun deleteLoginData(context: Context)
    fun getLocalSessionId(context: Context): String

    fun setThemeState(themeState: Boolean, context: Context)
    fun getTheme(context: Context): Boolean
}
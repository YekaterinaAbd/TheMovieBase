package com.example.movies.domain.repository

import com.example.movies.data.model.account.Account
import com.example.movies.data.model.account.LoginValidationData
import com.example.movies.data.model.account.Token

interface AccountRepository {
    suspend fun getSessionId(token: Token): String?
    suspend fun createToken(): Token?
    suspend fun validateWithLogin(data: LoginValidationData): Boolean
    suspend fun logOut(): Boolean

    fun getLocalUsername(): String
    fun getLocalPassword(): String
    fun hasSessionId(): Boolean
    fun saveLoginData(username: String, password: String, sessionId: String)
    fun deleteLoginData()
    fun getLocalSessionId(): String

    fun setThemeState(themeState: Boolean)
    fun getTheme(): Boolean
    suspend fun getAccountInfo(): Account?
}
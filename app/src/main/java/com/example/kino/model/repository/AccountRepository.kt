package com.example.kino.model.repository

import com.example.kino.model.account.LoginValidationData
import com.example.kino.model.account.Token
import com.example.kino.utils.PostApi

interface AccountRepository {
    suspend fun getSessionId(apiKey: String, token: Token): String?
    suspend fun createToken(apiKey: String): Token?
    suspend fun validateWithLogin(apiKey: String, data: LoginValidationData): Boolean
}

class AccountRepositoryImpl(private var service: PostApi) : AccountRepository {
    override suspend fun getSessionId(apiKey: String, token: Token): String? {
        return service.createSession(apiKey, token).body()?.sessionId
    }

    override suspend fun createToken(apiKey: String): Token? {
        return service.createRequestToken(apiKey).body()
    }

    override suspend fun validateWithLogin(apiKey: String, data: LoginValidationData): Boolean {
        return service.validateWithLogin(apiKey, data).isSuccessful
    }
}

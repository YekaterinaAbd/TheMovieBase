package com.example.movies.domain.use_case

import com.example.movies.data.model.account.LoginValidationData
import com.example.movies.data.model.account.Token
import com.example.movies.domain.repository.AccountRepository

class LoginUseCase(private val accountRepository: AccountRepository) {

    suspend fun createToken() = accountRepository.createToken()

    suspend fun validateWithLogin(loginValidationData: LoginValidationData) =
        accountRepository.validateWithLogin(loginValidationData)

    suspend fun getSessionId(token: Token) = accountRepository.getSessionId(token)

}
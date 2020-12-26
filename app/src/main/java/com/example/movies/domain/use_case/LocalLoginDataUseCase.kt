package com.example.movies.domain.use_case

import com.example.movies.domain.repository.AccountRepository

class LocalLoginDataUseCase(private val accountRepository: AccountRepository) {

    fun hasSessionId() = accountRepository.hasSessionId()

    fun saveLoginData(username: String, password: String, sessionId: String) =
        accountRepository.saveLoginData(username, password, sessionId)

    fun getLocalUsername() = accountRepository.getLocalUsername()

    fun getLocalPassword() = accountRepository.getLocalPassword()
}

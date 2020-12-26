package com.example.movies.domain.use_case

import com.example.movies.domain.repository.AccountRepository

class AccountUseCase(private val accountRepository: AccountRepository) {

    suspend fun getAccountInfo() = accountRepository.getAccountInfo()

    fun getUsername() = accountRepository.getLocalUsername()

    suspend fun logOut() = accountRepository.logOut()

    fun deleteLoginData() = accountRepository.deleteLoginData()

}
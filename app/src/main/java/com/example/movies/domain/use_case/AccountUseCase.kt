package com.example.movies.domain.use_case

import android.content.Context
import com.example.movies.data.network.API_KEY
import com.example.movies.domain.repository.AccountRepository

class AccountUseCase(private val accountRepository: AccountRepository) {
    fun getUsername(context: Context) = accountRepository.getLocalUsername(context)
    suspend fun logOut(context: Context) = accountRepository.logOut(API_KEY, context)
    fun deleteLoginData(context: Context) = accountRepository.deleteLoginData(context)
}
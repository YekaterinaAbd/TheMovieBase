package com.example.kino.domain.use_case

import android.content.Context
import com.example.kino.data.network.API_KEY
import com.example.kino.domain.repository.AccountRepository

class AccountUseCase(private val accountRepository: AccountRepository) {
    fun getUsername(context: Context) = accountRepository.getLocalUsername(context)
    suspend fun logOut(context: Context) = accountRepository.logOut(API_KEY, context)
    fun deleteLoginData(context: Context) = accountRepository.deleteLoginData(context)
}
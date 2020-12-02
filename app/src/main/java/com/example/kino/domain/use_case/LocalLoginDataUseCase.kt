package com.example.kino.domain.use_case

import android.content.Context
import com.example.kino.domain.repository.AccountRepository

class LocalLoginDataUseCase(private val accountRepository: AccountRepository) {

    fun hasSessionId(context: Context) = accountRepository.hasSessionId(context)

    fun saveLoginData(context: Context, username: String, password: String, sessionId: String) =
        accountRepository.saveLoginData(context, username, password, sessionId)

    fun getLocalUsername(context: Context) = accountRepository.getLocalUsername(context)

    fun getLocalPassword(context: Context) = accountRepository.getLocalPassword(context)
}
package com.example.movies.domain.use_case

import com.example.movies.domain.repository.AccountRepository

class SessionIdUseCase(
    private val accountRepository: AccountRepository
) {
    fun getLocalSessionId() = accountRepository.getLocalSessionId()
}

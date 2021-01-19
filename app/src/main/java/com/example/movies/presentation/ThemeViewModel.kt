package com.example.movies.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movies.domain.use_case.AccountUseCase

class ThemeViewModel(
    private val accountUseCase: AccountUseCase
) : ViewModel() {

    private val themeStateLiveData = MutableLiveData<Boolean>()

    fun isLoggedIn(): Boolean {
        return accountUseCase.isLoggedIn()
    }

//    fun setThemeState(themeState: Boolean) {
//        accountRepository.setThemeState(themeState)
//    }
//
//    fun getTheme() {
//        themeStateLiveData.value = accountRepository.getTheme()
//    }
}

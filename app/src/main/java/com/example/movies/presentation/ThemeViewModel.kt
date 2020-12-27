package com.example.movies.presentation

import androidx.lifecycle.MutableLiveData
import com.example.movies.core.base.BaseViewModel
import com.example.movies.domain.use_case.AccountUseCase

class ThemeViewModel(
    private val accountUseCase: AccountUseCase
) : BaseViewModel() {

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

package com.example.movies.presentation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.movies.core.base.BaseViewModel
import com.example.movies.domain.repository.AccountRepository

class ThemeViewModel(
    private val context: Context,
    private val accountRepository: AccountRepository
) : BaseViewModel() {

    val themeStateLiveData = MutableLiveData<Boolean>()

    fun setThemeState(themeState: Boolean) {
        accountRepository.setThemeState(themeState)
    }

    fun getTheme() {
        themeStateLiveData.value = accountRepository.getTheme()
    }
}

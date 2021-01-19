package com.example.movies.presentation.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movies.domain.use_case.AccountUseCase
import com.example.movies.presentation.ui.AccountState
import kotlinx.coroutines.launch

class AccountViewModel(
    private val accountUseCase: AccountUseCase
) : ViewModel() {

    private val _liveData = MutableLiveData<AccountState>()
    val liveData: LiveData<AccountState>
        get() = _liveData

    init {
        getAccountInfo()
    }

    private fun getUsername() {
        val username = accountUseCase.getUsername()
        _liveData.value = AccountState.AccountLocalResult(username)
    }

    private fun getAccountInfo() {
        viewModelScope.launch {
            val result = accountUseCase.getAccountInfo()
            if (result != null) _liveData.value = AccountState.AccountResult(result)
            else getUsername()
        }
    }

    fun logOut() {
        viewModelScope.launch {
            if (accountUseCase.logOut()) {
                deleteLogInData()
                _liveData.value = AccountState.LogOutSuccessful
            } else {
                _liveData.value = AccountState.LogOutFailed
            }
        }
    }

    private fun deleteLogInData() {
        accountUseCase.deleteLoginData()
    }
}

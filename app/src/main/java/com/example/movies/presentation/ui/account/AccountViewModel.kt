package com.example.movies.presentation.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.movies.core.base.BaseViewModel
import com.example.movies.data.model.account.Account
import com.example.movies.domain.use_case.AccountUseCase
import kotlinx.coroutines.launch

class AccountViewModel(
    private val accountUseCase: AccountUseCase
) : BaseViewModel() {

    // var username = DEFAULT_VALUE

    private val _liveData = MutableLiveData<State>()
    val liveData: LiveData<State>
        get() = _liveData

    init {
        //getUsername()
        getAccountInfo()
    }

    private fun getUsername() {
        val username = accountUseCase.getUsername()
        _liveData.value = State.AccountLocalResult(username)
    }

    private fun getAccountInfo() {
        uiScope.launch {
            val result = accountUseCase.getAccountInfo()
            if (result != null) _liveData.value = State.AccountResult(result)
            else getUsername()
        }
    }

    fun logOut() {
        uiScope.launch {
            if (accountUseCase.logOut()) {
                deleteLogInData()
                _liveData.value = State.LogOutSuccessful
            } else {
                _liveData.value = State.LogOutFailed
            }
        }
    }

    private fun deleteLogInData() {
        accountUseCase.deleteLoginData()
    }

    sealed class State {
        data class AccountResult(val data: Account) : State()
        data class AccountLocalResult(val username: String) : State()
        object LogOutSuccessful : State()
        object LogOutFailed : State()
    }
}

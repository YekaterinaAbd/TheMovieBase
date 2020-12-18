package com.example.movies.presentation.ui.account

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.movies.domain.use_case.AccountUseCase
import com.example.movies.presentation.BaseViewModel
import kotlinx.coroutines.launch

class AccountViewModel(
    private val context: Context,
    private val accountUseCase: AccountUseCase
) : BaseViewModel() {

    val username = MutableLiveData<String>()
    val liveData = MutableLiveData<State>()

    init {
        getUsername()
    }

    private fun getUsername() {
        username.value = accountUseCase.getUsername(context)
    }

    fun logOut() {
        uiScope.launch {
            val logOutSuccessful = accountUseCase.logOut(context)
            if (logOutSuccessful == true) {
                deleteLogInData()
                liveData.value = State.LogOutSuccessful
            } else {
                liveData.value = State.LogOutFailed
            }
        }
    }

    private fun deleteLogInData() {
        accountUseCase.deleteLoginData(context)
    }

    sealed class State {
        object LogOutSuccessful : State()
        object LogOutFailed : State()
    }
}

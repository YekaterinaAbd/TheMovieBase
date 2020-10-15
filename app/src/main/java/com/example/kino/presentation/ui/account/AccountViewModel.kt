package com.example.kino.presentation.ui.account

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.kino.data.network.API_KEY
import com.example.kino.domain.repository.AccountRepository
import com.example.kino.presentation.BaseViewModel
import kotlinx.coroutines.launch

class AccountViewModel(
    private val context: Context,
    private val accountRepository: AccountRepository
) : BaseViewModel() {

    val username = MutableLiveData<String>()
    val liveData = MutableLiveData<State>()

    init {
        getUsername()
    }

    private fun getUsername() {
        username.value = accountRepository.getLocalUsername(context)
    }

    fun logOut() {
        launch {
            try {
                val logOutSuccessful = accountRepository.logOut(API_KEY, context)
                if (logOutSuccessful != null) {
                    if (logOutSuccessful) {
                        deleteLogInData()
                        liveData.value = State.LogOutSuccessful
                    } else {
                        liveData.value = State.LogOutFailed
                    }
                }
            } catch (e: Exception) {
                liveData.value = State.LogOutFailed
            }
        }
    }

    private fun deleteLogInData() {
        accountRepository.deleteLoginData(context)
    }

    sealed class State {
        object LogOutSuccessful : State()
        object LogOutFailed : State()
    }
}

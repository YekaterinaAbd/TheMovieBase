package com.example.kino.presentation.ui.sign_in

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.kino.data.network.API_KEY
import com.example.kino.data.model.account.LoginValidationData
import com.example.kino.data.model.account.Token
import com.example.kino.domain.repository.AccountRepository
import com.example.kino.presentation.BaseViewModel
import kotlinx.coroutines.launch

class SignInViewModel(
    private val context: Context,
    private var accountRepository: AccountRepository
) : BaseViewModel() {
    private lateinit var loginValidationData: LoginValidationData
    private var token: Token? = null
    private var sessionId: String = ""
    private var username: String = ""
    private var password: String = ""

    val liveData = MutableLiveData<State>()

    init {
        if (accountRepository.hasSessionId(context)) liveData.value = State.Result
    }

    fun createTokenRequest(receivedUsername: String, receivedPassword: String) {
        launch {
            liveData.value = State.ShowLoading
            try {
                token = accountRepository.createToken(API_KEY)
                username = receivedUsername
                password = receivedPassword

                if (token != null) {
                    loginValidationData = LoginValidationData(
                        username,
                        password,
                        token!!.token
                    )
                    validateWithLogin()
                } else {
                    liveData.value = State.FailedLoading
                    liveData.value = State.HideLoading
                }

            } catch (e: Exception) {
                liveData.value = State.FailedLoading
                liveData.value = State.HideLoading
            }
        }
    }

    private fun validateWithLogin() {
        launch {
            try {
                val response = accountRepository.validateWithLogin(API_KEY, loginValidationData)
                if (response) {
                    createSession()
                } else {
                    liveData.value = State.WrongDataProvided
                    liveData.value = State.HideLoading
                }
            } catch (e: Exception) {
                liveData.value = State.WrongDataProvided
                liveData.value = State.HideLoading
            }
        }
    }

    private fun createSession() {
        launch {
            liveData.value = State.ShowLoading
            try {
                sessionId =
                    token?.let { accountRepository.getSessionId(API_KEY, it).toString() }
                        .toString()
                saveLoginData()
                liveData.value = State.HideLoading
                liveData.value = State.Result

            } catch (e: Exception) {
                liveData.value = State.FailedLoading
                liveData.value = State.HideLoading
            }
        }
    }

    private fun saveLoginData() {
        accountRepository.saveLoginData(context, username, password, sessionId)
    }

    fun getSavedUsername(): String {
        return accountRepository.getLocalUsername(context)
    }

    fun getSavedPassword(): String {
        return accountRepository.getLocalPassword(context)
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        object FailedLoading : State()
        object WrongDataProvided : State()
        object Result : State()
    }
}

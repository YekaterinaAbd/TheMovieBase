package com.example.movies.presentation.ui.sign_in

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movies.data.model.account.LoginValidationData
import com.example.movies.data.model.account.Token
import com.example.movies.domain.use_case.LocalLoginDataUseCase
import com.example.movies.domain.use_case.LoginUseCase
import com.example.movies.presentation.ui.SignInState
import kotlinx.coroutines.launch

class SignInViewModel(
    private val loginUseCase: LoginUseCase,
    private val localLoginDataUseCase: LocalLoginDataUseCase
) : ViewModel() {

    private lateinit var loginValidationData: LoginValidationData
    private var token: Token? = null
    private var sessionId: String? = ""
    private var username: String = ""
    private var password: String = ""

    val liveData = MutableLiveData<SignInState>()

    init {
        if (localLoginDataUseCase.hasSessionId()) liveData.value = SignInState.Result
    }

    fun createTokenRequest(receivedUsername: String, receivedPassword: String) {
        viewModelScope.launch {
            liveData.value = SignInState.ShowLoading
            token = loginUseCase.createToken()
            username = receivedUsername
            password = receivedPassword

            if (token != null) {
                loginValidationData = LoginValidationData(username, password, token!!.token)
                validateWithLogin()
            } else {
                liveData.value = SignInState.FailedLoading
                liveData.value = SignInState.HideLoading
            }
        }
    }

    private fun validateWithLogin() {
        viewModelScope.launch {
            val response = loginUseCase.validateWithLogin(loginValidationData)
            if (response) {
                createSession()
            } else {
                liveData.value = SignInState.WrongDataProvided
                liveData.value = SignInState.HideLoading
            }
        }
    }

    private fun createSession() {
        viewModelScope.launch {
            liveData.value = SignInState.ShowLoading
            sessionId = token?.let { loginUseCase.getSessionId(it) }
            if (!sessionId.isNullOrEmpty()) {
                saveLoginData()
                liveData.value = SignInState.HideLoading
                liveData.value = SignInState.Result
            } else {
                liveData.value = SignInState.FailedLoading
                liveData.value = SignInState.HideLoading
            }
        }
    }

    private fun saveLoginData() {
        sessionId?.let { localLoginDataUseCase.saveLoginData(username, password, it) }
    }

    fun getSavedUsername(): String {
        return localLoginDataUseCase.getLocalUsername()
    }

    fun getSavedPassword(): String {
        return localLoginDataUseCase.getLocalPassword()
    }
}

package com.example.movies.presentation.ui.sign_in

import androidx.lifecycle.MutableLiveData
import com.example.movies.core.base.BaseViewModel
import com.example.movies.data.model.account.LoginValidationData
import com.example.movies.data.model.account.Token
import com.example.movies.domain.use_case.LocalLoginDataUseCase
import com.example.movies.domain.use_case.LoginUseCase
import kotlinx.coroutines.launch

class SignInViewModel(
    private val loginUseCase: LoginUseCase,
    private val localLoginDataUseCase: LocalLoginDataUseCase
) : BaseViewModel() {

    private lateinit var loginValidationData: LoginValidationData
    private var token: Token? = null
    private var sessionId: String? = ""
    private var username: String = ""
    private var password: String = ""

    val liveData = MutableLiveData<State>()

    init {
        if (localLoginDataUseCase.hasSessionId()) liveData.value = State.Result
    }

    fun createTokenRequest(receivedUsername: String, receivedPassword: String) {
        uiScope.launch {
            liveData.value = State.ShowLoading
            token = loginUseCase.createToken()
            username = receivedUsername
            password = receivedPassword

            if (token != null) {
                loginValidationData = LoginValidationData(username, password, token!!.token)
                validateWithLogin()
            } else {
                liveData.value = State.FailedLoading
                liveData.value = State.HideLoading
            }
        }
    }

    private fun validateWithLogin() {
        uiScope.launch {
            val response = loginUseCase.validateWithLogin(loginValidationData)
            if (response) {
                createSession()
            } else {
                liveData.value = State.WrongDataProvided
                liveData.value = State.HideLoading
            }
        }
    }

    private fun createSession() {
        uiScope.launch {
            liveData.value = State.ShowLoading
            sessionId = token?.let { loginUseCase.getSessionId(it) }
            if (!sessionId.isNullOrEmpty()) {
                saveLoginData()
                liveData.value = State.HideLoading
                liveData.value = State.Result
            } else {
                liveData.value = State.FailedLoading
                liveData.value = State.HideLoading
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

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        object FailedLoading : State()
        object WrongDataProvided : State()
        object Result : State()
    }
}

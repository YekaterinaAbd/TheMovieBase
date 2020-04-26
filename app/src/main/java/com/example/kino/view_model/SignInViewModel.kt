package com.example.kino.view_model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.example.kino.R
import com.example.kino.model.account.LoginValidationData
import com.example.kino.model.account.Token
import com.example.kino.utils.RetrofitService
import com.example.kino.utils.apiKey
import kotlinx.coroutines.launch

class SignInViewModel(private val context: Context) : BaseViewModel() {
    private lateinit var loginValidationData: LoginValidationData
    private lateinit var token: Token
    private var sessionId: String = ""
    private var receivedToken: String = ""
    private var username: String = ""
    private var password: String = ""

    val liveData = MutableLiveData<State>()
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file),
        Context.MODE_PRIVATE
    )

    init {
        if (sharedPreferences.contains(context.getString(R.string.session_id))) {
            liveData.value = State.Result
        }
    }

    fun createTokenRequest(receivedUsername: String, receivedPassword: String) {
        launch {
            liveData.value = State.ShowLoading
            try {
                val response = RetrofitService.getPostApi().createRequestToken(apiKey)
                if (response.isSuccessful) {
                    username = receivedUsername
                    password = receivedPassword
                    val requestedToken = response.body()
                    if (requestedToken != null) {
                        receivedToken = requestedToken.token
                        loginValidationData = LoginValidationData(
                            username,
                            password,
                            receivedToken
                        )
                        validateWithLogin()
                    }

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
                val response =
                    RetrofitService.getPostApi()
                        .validateWithLogin(apiKey, loginValidationData)
                if (response.isSuccessful) {
                    token = Token(receivedToken)
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
                val response = RetrofitService.getPostApi().createSession(apiKey, token)
                if (response.isSuccessful) {
                    sessionId = response.body()?.sessionId.toString()
                    saveToSharedPreferences()
                    liveData.value = State.HideLoading
                    liveData.value = State.Result
                }
            } catch (e: Exception) {
                liveData.value = State.FailedLoading
                liveData.value = State.HideLoading
            }
        }
    }

    private fun saveToSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.putString(context.getString(R.string.username), username)
        editor.putString(context.getString(R.string.session_id), sessionId)
        editor.putString(context.getString(R.string.password), password)
        editor.apply()
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        object FailedLoading : State()
        object WrongDataProvided : State()
        object Result : State()
    }
}
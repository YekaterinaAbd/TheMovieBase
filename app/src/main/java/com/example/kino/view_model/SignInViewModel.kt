package com.example.kino.view_model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.R
import com.example.kino.model.ApiKey
import com.example.kino.model.RetrofitService
import com.example.kino.model.account.LoginValidationData
import com.example.kino.model.account.Token
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SignInViewModel(private val context: Context) : ViewModel(), CoroutineScope {
    private lateinit var loginValidationData: LoginValidationData
    private lateinit var token: Token
    private var sessionId: String = ""
    private var receivedToken: String = ""
    private var username: String = ""
    private var password: String = ""

    val liveData = MutableLiveData<State>()
    val signUpUrl: String = "https://www.themoviedb.org/account/signup"

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file),
        Context.MODE_PRIVATE
    )

    private val job = Job()


    init {
        if (sharedPreferences.contains(context.getString(R.string.session_id))) {
            liveData.value = State.Result
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun createTokenRequest(receivedUsername: String, receivedPassword: String) {
        launch {
            liveData.value = State.ShowLoading
            try {
                val response = RetrofitService.getPostApi().createRequestToken(ApiKey)
                if (response.isSuccessful) {
                    username = receivedUsername
                    password = receivedPassword
                    val requestedToken = response.body()
                    if (requestedToken != null) {
                        receivedToken = requestedToken.token
                        loginValidationData = LoginValidationData(
                            username,
                            password, receivedToken
                        )
                        validateWithLogin()
                    }

                } else {
                    liveData.value = State.FailedLoading
                }
            } catch (e: Exception) {
                liveData.value = State.FailedLoading
            }
        }
    }

    private fun validateWithLogin() {
        launch {
            liveData.value = State.ShowLoading
            try {
                val response =
                    RetrofitService.getPostApi().validateWithLogin(ApiKey, loginValidationData)
                if (response.isSuccessful) {
                    token = Token(receivedToken)
                    createSession()
                } else {
                    liveData.value = State.FailedLoading
                }
            } catch (e: Exception) {
                liveData.value = State.FailedLoading
            }
        }
    }

    private fun createSession() {
        launch {
            liveData.value = State.ShowLoading
            try {
                val response = RetrofitService.getPostApi().createSession(ApiKey, token)
                if (response.isSuccessful) {
                    sessionId = response.body()?.sessionId.toString()
                    saveToSharedPreferences()
                    liveData.value = State.HideLoading
                    liveData.value = State.Result
                }
            } catch (e: Exception) {
                liveData.value = State.FailedLoading
            }
        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        object FailedLoading : State()
        object Result : State()
    }

    private fun saveToSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.putString(context.getString(R.string.username), username)
        editor.putString(context.getString(R.string.session_id), sessionId)
        editor.putString(context.getString(R.string.password), password)
        editor.apply()
    }
}
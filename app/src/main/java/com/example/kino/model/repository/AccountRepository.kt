package com.example.kino.model.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.kino.R
import com.example.kino.model.account.LoginValidationData
import com.example.kino.model.account.Session
import com.example.kino.model.account.Token
import com.example.kino.utils.PostApi
import com.example.kino.utils.constants.DEFAULT_VALUE
import com.example.kino.utils.constants.NULLABLE_VALUE

interface AccountRepository {
    suspend fun getSessionId(apiKey: String, token: Token): String?
    suspend fun createToken(apiKey: String): Token?
    suspend fun validateWithLogin(apiKey: String, data: LoginValidationData): Boolean
    suspend fun logOut(apiKey: String, context: Context): Boolean?

    fun getUsername(context: Context): String
    fun hasSessionId(context: Context): Boolean
    fun saveLoginData(context: Context, username: String, password: String, sessionId: String)
    fun deleteLoginData(context: Context)
    fun getLocalSessionId(context: Context): String


    fun setThemeState(themeState: Boolean, context: Context)
    fun getTheme(context: Context): Boolean
}

class AccountRepositoryImpl(
    private var service: PostApi,
    private var sharedPreferences: SharedPreferences
) : AccountRepository {
    override suspend fun getSessionId(apiKey: String, token: Token): String? {
        return service.createSession(apiKey, token).body()?.sessionId
    }

    override suspend fun createToken(apiKey: String): Token? {
        return service.createRequestToken(apiKey).body()
    }

    override suspend fun validateWithLogin(apiKey: String, data: LoginValidationData): Boolean {
        return service.validateWithLogin(apiKey, data).isSuccessful
    }

    override suspend fun logOut(apiKey: String, context: Context): Boolean? {
        val session = Session(getLocalSessionId(context))
        return service.deleteSession(apiKey, session).body()?.success
    }

    override fun getUsername(context: Context): String {
        return if (sharedPreferences.contains(context.getString(R.string.username)))
            sharedPreferences.getString(
                context.getString(R.string.username), DEFAULT_VALUE
            ) as String
        else DEFAULT_VALUE
    }

    override fun hasSessionId(context: Context): Boolean {
        return sharedPreferences.contains(context.getString(R.string.session_id))
    }

    override fun getLocalSessionId(context: Context): String {
        return if (sharedPreferences.contains(context.getString(R.string.session_id))) {
            sharedPreferences.getString(
                context.getString(R.string.session_id), NULLABLE_VALUE
            ) as String
        } else NULLABLE_VALUE
    }

    override fun saveLoginData(
        context: Context, username: String, password: String, sessionId: String
    ) {
        val editor = sharedPreferences.edit()
        editor.putString(context.getString(R.string.username), username)
        editor.putString(context.getString(R.string.session_id), sessionId)
        editor.putString(context.getString(R.string.password), password)
        editor.apply()
    }

    override fun deleteLoginData(context: Context) {
        val editor = sharedPreferences.edit()
        editor.remove(context.getString(R.string.session_id))
        editor.apply()
    }

    override fun setThemeState(themeState: Boolean, context: Context) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.getString(R.string.theme_state), themeState)
        editor.apply()
    }

    override fun getTheme(context: Context): Boolean {
        return sharedPreferences.getBoolean(context.getString(R.string.theme_state), false)
    }

}

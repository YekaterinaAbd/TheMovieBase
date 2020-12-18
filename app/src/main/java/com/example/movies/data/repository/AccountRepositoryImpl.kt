package com.example.movies.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.movies.R
import com.example.movies.data.model.account.LoginValidationData
import com.example.movies.data.model.account.Session
import com.example.movies.data.model.account.Token
import com.example.movies.data.network.MovieApi
import com.example.movies.domain.repository.AccountRepository
import com.example.movies.presentation.utils.constants.DEFAULT_VALUE
import com.example.movies.presentation.utils.constants.NULLABLE_VALUE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountRepositoryImpl(
    private var service: MovieApi,
    private var sharedPreferences: SharedPreferences
) : AccountRepository {

    override suspend fun getSessionId(apiKey: String, token: Token): String? =
        withContext(Dispatchers.IO) {
            try {
                val response = service.createSession(apiKey, token)
                if (response.isSuccessful) return@withContext response.body()?.sessionId
                else return@withContext null
            } catch (e: Exception) {
                return@withContext null
            }
        }

    override suspend fun createToken(apiKey: String): Token? = withContext(Dispatchers.IO) {
        try {
            val response = service.createRequestToken(apiKey)
            if (response.isSuccessful) return@withContext response.body()
            else return@withContext null
        } catch (e: java.lang.Exception) {
            return@withContext null
        }
    }

    override suspend fun validateWithLogin(apiKey: String, data: LoginValidationData): Boolean =
        withContext(Dispatchers.IO) {
            try {
                return@withContext service.validateWithLogin(apiKey, data).isSuccessful
            } catch (e: Exception) {
                return@withContext false
            }
        }

    override suspend fun logOut(apiKey: String, context: Context): Boolean? {
        return try {
            val session = Session(getLocalSessionId(context))
            service.deleteSession(apiKey, session).body()?.success
        } catch (e: Exception) {
            false
        }
    }

    override fun getLocalPassword(context: Context): String {
        return if (sharedPreferences.contains(context.getString(R.string.password)))
            sharedPreferences.getString(
                context.getString(R.string.password), DEFAULT_VALUE
            ) as String
        else DEFAULT_VALUE
    }

    override fun getLocalUsername(context: Context): String {
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

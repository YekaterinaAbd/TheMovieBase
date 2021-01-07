package com.example.movies.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.movies.R
import com.example.movies.core.extensions.get
import com.example.movies.core.extensions.put
import com.example.movies.data.model.account.Account
import com.example.movies.data.model.account.LoginValidationData
import com.example.movies.data.model.account.Session
import com.example.movies.data.model.account.Token
import com.example.movies.data.network.API_KEY
import com.example.movies.data.network.AccountApi
import com.example.movies.domain.repository.AccountRepository
import com.example.movies.presentation.utils.constants.DEFAULT_VALUE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountRepositoryImpl(
    private val api: AccountApi,
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) : AccountRepository {

    override suspend fun getSessionId(token: Token): String? =
        withContext(Dispatchers.IO) {
            try {
                val response = api.createSession(API_KEY, token)
                if (response.isSuccessful) return@withContext response.body()?.sessionId
                else return@withContext null
            } catch (e: Exception) {
                return@withContext null
            }
        }

    override suspend fun createToken(): Token? = withContext(Dispatchers.IO) {
        try {
            val response = api.createRequestToken(API_KEY)
            if (response.isSuccessful) return@withContext response.body()
            else return@withContext null
        } catch (e: java.lang.Exception) {
            return@withContext null
        }
    }

    override suspend fun validateWithLogin(data: LoginValidationData): Boolean =
        withContext(Dispatchers.IO) {
            try {
                return@withContext api.validateWithLogin(API_KEY, data).isSuccessful
            } catch (e: Exception) {
                return@withContext false
            }
        }

    override suspend fun logOut(): Boolean {
        return try {
            val session = Session(getLocalSessionId())
            api.deleteSession(API_KEY, session).body()?.success ?: false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getAccountInfo(): Account? = withContext(Dispatchers.IO) {
        try {
            return@withContext api.getAccountInfo(API_KEY, getLocalSessionId()).body()
        } catch (e: Exception) {
            return@withContext null
        }
    }

    override fun getLocalPassword(): String {
        return sharedPreferences.get(context.getString(R.string.password), DEFAULT_VALUE)
    }

    override fun getLocalUsername(): String {
        return sharedPreferences.get(context.getString(R.string.username), DEFAULT_VALUE)
    }

    override fun hasSessionId(): Boolean {
        return sharedPreferences.contains(context.getString(R.string.session_id))
    }

    override fun getLocalSessionId(): String {
        return sharedPreferences.get(context.getString(R.string.session_id), DEFAULT_VALUE)
    }

    override fun saveLoginData(
        username: String, password: String, sessionId: String
    ) {
        sharedPreferences.put(
            context.getString(R.string.username) to username,
            context.getString(R.string.session_id) to sessionId,
            context.getString(R.string.password) to password
        )
    }

    override fun deleteLoginData() {
        val editor = sharedPreferences.edit()
        editor.remove(context.getString(R.string.session_id))
        editor.apply()
    }

    override fun setThemeState(themeState: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.getString(R.string.theme_state), themeState)
        editor.apply()
    }

    override fun getTheme(): Boolean {
        return sharedPreferences.getBoolean(context.getString(R.string.theme_state), false)
    }

}

package com.example.kino.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.kino.R
import com.example.kino.utils.AppContainer

class ThemeViewModel(private val context: Context) : BaseViewModel() {
    val themeStateLiveData = MutableLiveData<Boolean>()

    private val sharedPreferences = AppContainer.getPreferences()

    fun setThemeState(themeState: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.getString(R.string.theme_state), themeState)
        editor.apply()
    }

    fun getTheme() {
        themeStateLiveData.value =
            sharedPreferences.getBoolean(context.getString(R.string.theme_state), false)
    }
}
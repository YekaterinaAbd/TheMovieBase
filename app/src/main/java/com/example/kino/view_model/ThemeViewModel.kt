package com.example.kino.view_model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.example.kino.R
import com.example.kino.utils.constants.DEFAULT_THEME

class ThemeViewModel(private val context: Context) : BaseViewModel() {
    val themeStateLiveData = MutableLiveData<Boolean>()

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file),
        Context.MODE_PRIVATE
    )

    fun setThemeState(themeState:Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.getString(R.string.theme_state), themeState)
        editor.apply()
    }
    fun getTheme(){
        themeStateLiveData.value = sharedPreferences.getBoolean(context.getString(R.string.theme_state), false)
    }
}
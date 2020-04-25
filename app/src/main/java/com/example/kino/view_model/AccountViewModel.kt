package com.example.kino.view_model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.R

class AccountViewModel(private val context: Context) : ViewModel() {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file), Context.MODE_PRIVATE
    )
    private val defaultValue: String = "default"
    val liveData = MutableLiveData<String>()

    init {
        getUsername()
    }

    private fun getUsername() {
        if (sharedPreferences.contains(context.getString(R.string.username)))
            liveData.value =
                sharedPreferences.getString(context.getString(R.string.username), defaultValue)
    }
}
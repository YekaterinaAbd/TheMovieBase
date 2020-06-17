package com.example.kino.view_model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.CinemaApplication
import com.example.kino.R
import com.example.kino.utils.constants.DEFAULT_VALUE

class AccountViewModel(private val context: Context) : ViewModel() {

    val liveData = MutableLiveData<String>()

    init {
        getUsername()
    }

    private fun getUsername() {
        val sharedPreferences = CinemaApplication.appContainer.sharedPreferences
        if (sharedPreferences.contains(context.getString(R.string.username)))
            liveData.value = sharedPreferences.getString(
                context.getString(R.string.username), DEFAULT_VALUE
            )
    }
}

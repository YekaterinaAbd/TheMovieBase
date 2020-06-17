package com.example.kino.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.model.repository.AccountRepository

class AccountViewModel(
    private val context: Context,
    private val accountRepository: AccountRepository
) : ViewModel() {

    val liveData = MutableLiveData<String>()

    init {
        getUsername()
    }

    private fun getUsername() {
        liveData.value = accountRepository.getUsername(context)
    }
}

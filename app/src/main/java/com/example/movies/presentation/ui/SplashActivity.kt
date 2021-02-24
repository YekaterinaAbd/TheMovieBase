package com.example.movies.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.movies.core.extensions.navigateTo
import com.example.movies.presentation.ThemeViewModel
import com.example.movies.presentation.ui.sign_in.SignInActivity
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {

    private val viewModel: ThemeViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //    changeActivity()
    }

    private fun changeActivity() {
        if (viewModel.isLoggedIn()) navigateTo<MainActivity>()
        else navigateTo<SignInActivity>()
        finish()
    }
}
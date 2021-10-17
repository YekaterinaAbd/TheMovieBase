package com.example.movies.presentation.ui.sign_in

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doBeforeTextChanged
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.movies.R
import com.example.movies.core.extensions.navigateTo
import com.example.movies.core.extensions.toast
import com.example.movies.core.view.LoaderDialog
import com.example.movies.data.network.SIGN_UP_URL
import com.example.movies.databinding.ActivitySignInBinding
import com.example.movies.presentation.ui.MainActivity
import com.example.movies.presentation.ui.SignInState
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.android.inject

class SignInActivity : AppCompatActivity(R.layout.activity_sign_in) {

    private val binding by viewBinding(ActivitySignInBinding::bind)
    private var loader: LoaderDialog? = null

    private val signInViewModel: SignInViewModel by inject()

    private val topic = "movies"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppThemeLight)
        subscribeToTopic()
        bindViews()
        observeViewModel()
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    private fun bindViews() = with(binding) {
        editTextUsername.text = SpannableStringBuilder(signInViewModel.getSavedUsername())
        editTextPassword.text = SpannableStringBuilder(signInViewModel.getSavedPassword())

        editTextUsername.doBeforeTextChanged { _, _, _, _ ->
            hideDataError()
        }

        editTextPassword.doBeforeTextChanged { _, _, _, _ ->
            hideDataError()
        }

        textSignUpLink.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(SIGN_UP_URL))
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
            startActivity(browserIntent)
        }

        buttonSignIn.setOnClickListener {
            hideDataError()
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            signInViewModel.createTokenRequest(username, password)
        }
    }

    private fun observeViewModel() {
        signInViewModel.liveData.observe(this) { result ->
            showLoader(result is SignInState.ShowLoading)
            when (result) {
                is SignInState.FailedLoading -> {
                    toast(getString(R.string.error_occurred))
                }
                is SignInState.WrongDataProvided -> {
                    showDataError()
                }
                is SignInState.Result -> {
                    navigateTo<MainActivity>()
                    finish()
                }
            }
        }
    }

    private fun showLoader(show: Boolean) {
        if (loader == null) {
            loader = LoaderDialog(this)
        }
        loader?.showOrHide(show)
    }

    private fun showDataError() {
        binding.layoutUsername.error = " "
        binding.layoutPassword.error = " "
    }

    private fun hideDataError() {
        binding.layoutUsername.error = null
        binding.layoutPassword.error = null
    }
}

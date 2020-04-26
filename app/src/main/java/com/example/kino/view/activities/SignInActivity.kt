package com.example.kino.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.kino.R
import com.example.kino.utils.signUpUrl
import com.example.kino.view_model.SignInViewModel
import com.example.kino.view_model.ViewModelProviderFactory


class SignInActivity : AppCompatActivity() {


    private lateinit var wrongDataText: TextView
    private lateinit var signInButton: Button
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var registrationLink: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var signInViewModel: SignInViewModel
    private lateinit var viewModelProviderFactory: ViewModelProviderFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setViewModel()
        signInProcessing()
        bindViews()
    }

    private fun setViewModel() {
        viewModelProviderFactory = ViewModelProviderFactory(context = this)
        signInViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(SignInViewModel::class.java)
    }

    private fun bindViews() {
        username = findViewById(R.id.evUsername)
        password = findViewById(R.id.evPassword)
        signInButton = findViewById(R.id.btnSignIn)
        wrongDataText = findViewById(R.id.tvWrongData)
        registrationLink = findViewById(R.id.tvAccountLink)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        registrationLink.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(signUpUrl))
            startActivity(browserIntent)
        }

        signInButton.setOnClickListener {
            signInProcessing()
            signInViewModel.createTokenRequest(username.text.toString(), password.text.toString())
        }
    }

    private fun signInProcessing() {
        signInViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is SignInViewModel.State.ShowLoading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is SignInViewModel.State.HideLoading -> {
                    progressBar.visibility = View.GONE
                }
                is SignInViewModel.State.FailedLoading -> {
                    Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT)
                        .show()
                }
                is SignInViewModel.State.WrongDataProvided -> {
                    wrongDataText.text = getString(R.string.wrong_data)
                }
                is SignInViewModel.State.Result -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }
}

package com.example.kino.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kino.CinemaApplication
import com.example.kino.R
import com.example.kino.model.repository.AccountRepository
import com.example.kino.utils.constants.SIGNED_IN
import com.example.kino.utils.constants.SIGN_UP_URL
import com.example.kino.view_model.MarkersViewModel
import com.example.kino.view_model.SignInViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging


class SignInActivity : AppCompatActivity() {

    private lateinit var wrongDataText: TextView
    private lateinit var signInButton: Button
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var registrationLink: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var signInRepository: AccountRepository
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var markersViewModel: MarkersViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val topic = "movies"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        subscribeToTopic()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setViewModel()
        markersViewModel.fillDatabase()
        signInProcessing()
        bindViews()
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    private fun setViewModel() {
        val appContainer = CinemaApplication.appContainer
        signInViewModel = SignInViewModel(this, appContainer.accountRepository)
        markersViewModel = appContainer.movieViewModelFactory.create(MarkersViewModel::class.java)
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
                Intent(Intent.ACTION_VIEW, Uri.parse(SIGN_UP_URL))
            startActivity(browserIntent)
        }

        signInButton.setOnClickListener {
            signInProcessing()
            signInViewModel.createTokenRequest(username.text.toString(), password.text.toString())

            val bundle = Bundle()
            firebaseAnalytics.logEvent(SIGNED_IN, bundle)
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

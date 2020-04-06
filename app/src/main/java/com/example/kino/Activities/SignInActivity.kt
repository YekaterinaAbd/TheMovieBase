package com.example.kino.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.kino.AccountClasses.LoginValidationData
import com.example.kino.AccountClasses.SessionResult
import com.example.kino.AccountClasses.TokenResult
import com.example.kino.ApiKey
import com.example.kino.R
import com.example.kino.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    lateinit var requestToken: String
    lateinit var loginValidationData: LoginValidationData
    lateinit var tokenResult: TokenResult
    private lateinit var sharedPref:SharedPreferences
    lateinit var wrongData: TextView
    private lateinit var signInButton: Button
    private lateinit var tvUsername: EditText
    private lateinit var tvPassword: EditText
    private lateinit var registrationLink: TextView
    private lateinit var progressBar: ProgressBar

    var sessionId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sharedPref = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

        if(sharedPref.contains(getString(R.string.session_id))){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        bindViews()
    }

    private fun bindViews(){
        tvUsername = findViewById(R.id.evUsername)
        tvPassword = findViewById(R.id.evPassword)
        signInButton = findViewById(R.id.btnSignIn)
        wrongData = findViewById(R.id.tvWrongData)
        registrationLink = findViewById(R.id.tvAccountLink)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        registrationLink.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themoviedb.org/account/signup"))
            startActivity(browserIntent)
        }

        signInButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            createRequestToken()
        }
    }



    private fun createRequestToken() {
        RetrofitService.getPostApi().createRequestToken(ApiKey).enqueue(object :
            Callback<TokenResult> {

            override fun onFailure(call: Call<TokenResult>, t: Throwable) {
                Toast.makeText(this@SignInActivity, "Error occured", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                requestToken = ""
            }

            override fun onResponse(call: Call<TokenResult>, response: Response<TokenResult>) {
                if(response.isSuccessful){
                    val ans = response.body()
                    if (ans != null) {
                        requestToken = ans.requestToken
                        loginValidationData = LoginValidationData(tvUsername.text.toString(),
                            tvPassword.text.toString(), requestToken)

                        validateWithLogin()
                    }
                }
                else{
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun validateWithLogin(){
        RetrofitService.getPostApi().validateWithLogin(ApiKey, loginValidationData).enqueue(object :
            Callback<TokenResult> {
            override fun onFailure(call: Call<TokenResult>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@SignInActivity, "Error occurred", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<TokenResult>, response: Response<TokenResult>) {
                if(response.isSuccessful) {
                    tokenResult = TokenResult(requestToken)
                    createSession()
                }
               else{
                    wrongData.text = "Wrong data"
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun createSession(){
        RetrofitService.getPostApi().createSession(ApiKey, tokenResult).enqueue(object :
            Callback<SessionResult> {
            override fun onFailure(call: Call<SessionResult>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@SignInActivity, "Error occurred", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<SessionResult>, response: Response<SessionResult>) {
                if(response.isSuccessful) {
                    sessionId = response.body()?.sessionId.toString()

                    saveToPreferences()

                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }

    private fun saveToPreferences() {

        val editor = sharedPref.edit()
        editor.putString(getString(R.string.username), tvUsername.text.toString())
        editor.putString(getString(R.string.session_id), sessionId) // change to sessionId?
        editor.apply()
    }
}

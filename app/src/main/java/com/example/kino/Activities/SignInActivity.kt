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
import com.example.kino.AccountClasses.Session
import com.example.kino.AccountClasses.Token
import com.example.kino.ApiKey
import com.example.kino.R
import com.example.kino.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    lateinit var receivedToken: String
    lateinit var loginValidationData: LoginValidationData
    lateinit var token: Token
    private lateinit var sharedPreferences:SharedPreferences
    lateinit var wrongDataText: TextView
    private lateinit var signInButton: Button
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var registrationLink: TextView
    private lateinit var progressBar: ProgressBar

    var sessionId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

        if(sharedPreferences.contains(getString(R.string.session_id))){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        bindViews()
    }

    private fun bindViews(){
        username = findViewById(R.id.evUsername)
        password = findViewById(R.id.evPassword)
        signInButton = findViewById(R.id.btnSignIn)
        wrongDataText = findViewById(R.id.tvWrongData)
        registrationLink = findViewById(R.id.tvAccountLink)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        registrationLink.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themoviedb.org/account/signup"))
            startActivity(browserIntent)
        }

        signInButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            createTokenRequest()
        }
    }


    private fun createTokenRequest() {
        RetrofitService.getPostApi().createRequestToken(ApiKey).enqueue(object :
            Callback<Token> {

            override fun onFailure(call: Call<Token>, t: Throwable) {
                Toast.makeText(this@SignInActivity, "Error occured", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                receivedToken = ""
            }

            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if(response.isSuccessful){
                    val requestedToken = response.body()
                    if (requestedToken != null) {
                        receivedToken = requestedToken.token
                        loginValidationData = LoginValidationData(username.text.toString(),
                            password.text.toString(), receivedToken)
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
            Callback<Token> {
            override fun onFailure(call: Call<Token>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@SignInActivity, "Error occurred", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if(response.isSuccessful) {
                    token = Token(receivedToken)
                    createSession()
                }
               else{
                    wrongDataText.text = "Wrong data"
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun createSession(){
        RetrofitService.getPostApi().createSession(ApiKey, token).enqueue(object :
            Callback<Session> {
            override fun onFailure(call: Call<Session>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@SignInActivity, "Error occurred", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Session>, response: Response<Session>) {
                if(response.isSuccessful) {
                    sessionId = response.body()?.sessionId.toString()

                    saveToSharedPreferences()

                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }

    private fun saveToSharedPreferences() {

        val editor = sharedPreferences.edit()
        editor.putString(getString(R.string.username), username.text.toString())
        editor.putString(getString(R.string.session_id), sessionId)
        editor.apply()
    }
}

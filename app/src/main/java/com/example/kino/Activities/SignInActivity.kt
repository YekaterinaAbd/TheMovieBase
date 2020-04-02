package com.example.kino.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.kino.AccountClasses.LoginValidationData
import com.example.kino.AccountClasses.SessionResult
import com.example.kino.AccountClasses.TokenResult
import com.example.kino.R
import com.example.kino.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    private val API_KEY: String = "d118a5a4e56930c8ce9bd2321609d877"
    lateinit var requestToken: String
    lateinit var loginValidationData: LoginValidationData
    lateinit var tokenResult: TokenResult
    lateinit var sharedPref:SharedPreferences
    var sessionId: String = ""

    private lateinit var signInButton: Button
    private lateinit var tvUsername: EditText
    private lateinit var tvPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        tvUsername = findViewById(R.id.tvUsername)
        tvPassword = findViewById(R.id.tvPassword)
        signInButton = findViewById(R.id.button_sign_in)


        sharedPref = getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)

        //createRequestToken()
        signInButton.setOnClickListener {
            createRequestToken()

        }
    }

    private fun createRequestToken() {
        RetrofitService.getPostApi().createRequestToken(API_KEY).enqueue(object :
            Callback<TokenResult> {
            override fun onFailure(call: Call<TokenResult>, t: Throwable) {
                Toast.makeText(this@SignInActivity, "Error occured", Toast.LENGTH_SHORT).show()
                requestToken = ""
            }

            override fun onResponse(call: Call<TokenResult>, response: Response<TokenResult>) {
                if(response.isSuccessful){
                    val ans = response.body()
                    if (ans != null) {
                        requestToken = ans.request_token
                        Log.d("error", requestToken)

                        loginValidationData = LoginValidationData(tvUsername.text.toString(),
                            tvPassword.text.toString(), requestToken)

                        validateWithLogin()
                    }
                }
            }

        })
    }

    private fun validateWithLogin(){
        RetrofitService.getPostApi().validateWithLogin(API_KEY, loginValidationData).enqueue(object :
            Callback<TokenResult> {
            override fun onFailure(call: Call<TokenResult>, t: Throwable) {
                Toast.makeText(this@SignInActivity, "Error occurred", Toast.LENGTH_SHORT).show()
                Log.d("error", t.printStackTrace().toString())
            }

            override fun onResponse(call: Call<TokenResult>, response: Response<TokenResult>) {
                if(response.isSuccessful) {
                    Toast.makeText(this@SignInActivity, "Great", Toast.LENGTH_SHORT).show()
                    Log.d("error", response.body().toString())
                    tokenResult = TokenResult(requestToken)
                    createSession()
                }
            }

        })
    }

    private fun createSession(){
        RetrofitService.getPostApi().createSession(API_KEY, tokenResult).enqueue(object :
            Callback<SessionResult> {
            override fun onFailure(call: Call<SessionResult>, t: Throwable) {
                Toast.makeText(this@SignInActivity, "Error occurred", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<SessionResult>, response: Response<SessionResult>) {
                if(response.isSuccessful) {
                    sessionId = response.body()?.session_id.toString()
                    Log.d("error", sessionId)

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
        editor.putString(getString(R.string.password), tvPassword.text.toString())
        editor.putString(getString(R.string.session_id), sessionId)
        editor.apply()
    }
}

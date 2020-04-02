package com.example.kino.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kino.R



class AccountFragment: Fragment() {

    lateinit var usernameData: TextView
    lateinit var sessionIdData: TextView
    lateinit var sharedPref: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.account_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameData = view.findViewById(R.id.usernameData)
        sessionIdData = view.findViewById(R.id.session_id)

        sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE
        )!!

        if (sharedPref.contains("username"))
            usernameData.text = sharedPref.getString("username", "null")
        if(sharedPref.contains(getString(R.string.session_id)))
            sessionIdData.text = sharedPref.getString(getString(R.string.session_id), "null")
    }
}
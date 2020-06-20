package com.example.kino.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kino.R
import com.example.kino.view.activities.GoogleMapsActivity
import com.example.kino.view.activities.SignInActivity
import com.example.kino.view_model.AccountViewModel
import org.koin.android.ext.android.inject

class AccountFragment : Fragment() {

    private lateinit var username: TextView
    private val accountViewModel: AccountViewModel by inject()
    private lateinit var logOutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.account_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
    }


    private fun bindViews(view: View) = with(view) {
        username = view.findViewById(R.id.tvUsernameData)
        logOutButton = view.findViewById(R.id.btnLogOut)
        username.text = accountViewModel.username.value

        val map = view.findViewById<Button>(R.id.map)
        map.setOnClickListener {
            val intent = Intent(requireActivity(), GoogleMapsActivity::class.java)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {
            logOut()
        }
    }

    private fun logOut() {
        accountViewModel.logOut()
        accountViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is AccountViewModel.State.LogOutSuccessful -> {
                    val intent = Intent(requireContext(), SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    requireActivity().finish()
                }
                is AccountViewModel.State.LogOutFailed -> {
                    Toast.makeText(requireContext(), "Log out failed", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}

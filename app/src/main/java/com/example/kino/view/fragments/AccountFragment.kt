package com.example.kino.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kino.R
import com.example.kino.view.activities.GoogleMapsActivity
import com.example.kino.view_model.AccountViewModel
import com.example.kino.view_model.ViewModelProviderFactory

class AccountFragment : Fragment() {

    private lateinit var username: TextView
    private lateinit var viewModelProviderFactory: ViewModelProviderFactory
    private lateinit var accountViewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.account_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        bindViews(view)
    }

    private fun setViewModel() {
        viewModelProviderFactory = ViewModelProviderFactory(context = requireContext())
        accountViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(AccountViewModel::class.java)
    }

    private fun bindViews(view: View) = with(view) {
        username = view.findViewById(R.id.tvUsernameData)
        username.text = accountViewModel.liveData.value

        val map = view.findViewById<Button>(R.id.map)
        map.setOnClickListener {
            val intent = Intent(requireActivity(), GoogleMapsActivity::class.java)
            startActivity(intent)
        }
    }
}

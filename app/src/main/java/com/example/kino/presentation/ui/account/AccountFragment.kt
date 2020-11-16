package com.example.kino.presentation.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kino.R
import com.example.kino.presentation.ui.lists.MoviesType
import com.example.kino.presentation.ui.lists.favouries.FavouritesFragment
import com.example.kino.presentation.ui.markers.GoogleMapsActivity
import com.example.kino.presentation.ui.sign_in.SignInActivity
import org.koin.android.ext.android.inject

class AccountFragment : Fragment() {

    private lateinit var username: TextView
    private val accountViewModel: AccountViewModel by inject()
    private lateinit var viewFavourites: CardView
    private lateinit var viewWatchList: CardView
    private lateinit var logOutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
    }

    private fun bindViews(view: View) = with(view) {
        username = view.findViewById(R.id.toolbar)
        logOutButton = view.findViewById(R.id.btnLogOut)
        username.text = accountViewModel.username.value

        viewFavourites = view.findViewById(R.id.btnFavourites)
        viewWatchList = view.findViewById(R.id.btnWatchList)

        viewFavourites.setOnClickListener {
            openListFragment(MoviesType.FAVOURITES)
        }

        viewWatchList.setOnClickListener {

        }

        val viewCinemaMap = view.findViewById<CardView>(R.id.btnCinemaMap)
        viewCinemaMap.setOnClickListener {
            val intent = Intent(requireActivity(), GoogleMapsActivity::class.java)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {
            logOut()
        }
    }

    private fun openListFragment(type: MoviesType) {
        val bundle = Bundle()
        bundle.putSerializable("type", type)

        val movieListsFragment = FavouritesFragment()
        movieListsFragment.arguments = bundle
        parentFragmentManager.beginTransaction().add(R.id.framenav, movieListsFragment)
            .addToBackStack(null).commit()
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

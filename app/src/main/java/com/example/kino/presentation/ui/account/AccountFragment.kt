package com.example.kino.presentation.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kino.R
import com.example.kino.data.model.movie.MoviesType
import com.example.kino.presentation.ui.MainActivity
import com.example.kino.presentation.ui.lists.favouries.FavouritesFragment
import com.example.kino.presentation.ui.markers.GoogleMapsActivity
import com.example.kino.presentation.ui.sign_in.SignInActivity
import com.example.kino.presentation.utils.constants.MOVIE_TYPE
import org.koin.android.ext.android.inject

class AccountFragment : Fragment() {

    private lateinit var username: androidx.appcompat.widget.Toolbar
    private val accountViewModel: AccountViewModel by inject()
    private lateinit var viewFavourites: ConstraintLayout
    private lateinit var viewWatchList: ConstraintLayout
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
        //username.text = accountViewModel.username.value
        (activity as MainActivity).setSupportActionBar(username)
        (activity as MainActivity).supportActionBar?.title = accountViewModel.username.value
        viewFavourites = view.findViewById(R.id.btnFavourites)
        viewWatchList = view.findViewById(R.id.btnLike)

        viewFavourites.setOnClickListener {
            openListFragment(MoviesType.FAVOURITES)
        }

        viewWatchList.setOnClickListener {
            openListFragment(MoviesType.WATCH_LIST)
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
        bundle.putSerializable(MOVIE_TYPE, type)

//        val movieListsFragment = TopFilmsFragment()
//        movieListsFragment.arguments = bundle
//        parentFragmentManager.beginTransaction().add(R.id.framenav, movieListsFragment)
//            .addToBackStack(null).commit()

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

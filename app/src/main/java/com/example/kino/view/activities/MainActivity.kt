package com.example.kino.view.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.kino.R
import com.example.kino.utils.constants.FAVOURITES_PAGE_CLICKED
import com.example.kino.utils.constants.MAIN_PAGE_CLICKED
import com.example.kino.utils.constants.PROFILE_PAGE_CLICKED
import com.example.kino.view.fragments.AccountFragment
import com.example.kino.view.fragments.FavouritesFragment
import com.example.kino.view.fragments.FilmsFragment
import com.example.kino.view.fragments.MovieDetailsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: TextView
    private val fragmentManager: FragmentManager = supportFragmentManager
    private var activeFragment: Fragment = FilmsFragment()
    private var filmsFragment: Fragment = FilmsFragment()
    private var favouritesFragment: Fragment = FavouritesFragment()
    private var accountFragment: Fragment = AccountFragment()
    private var movieDetailsFragment: Fragment = MovieDetailsFragment()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener(navListener)
        hidingFragments()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        toolbar.visibility = View.VISIBLE
        bottomNavigation.visibility = View.VISIBLE
    }
    private fun logEvent(logMessage: String) {
        val bundle = Bundle()
        firebaseAnalytics.logEvent(logMessage, bundle)
    }

    private fun hidingFragments() {
        fragmentManager.beginTransaction().add(R.id.frame, filmsFragment).commit()
        fragmentManager.beginTransaction().add(R.id.frame, favouritesFragment)
            .hide(favouritesFragment).commit()
        fragmentManager.beginTransaction().add(R.id.frame, accountFragment).hide(accountFragment)
            .commit()
        fragmentManager.beginTransaction().add(R.id.frame, movieDetailsFragment).hide(movieDetailsFragment)
    }

    private val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.films -> {
                    logEvent(MAIN_PAGE_CLICKED)
                    fragmentManager.beginTransaction().hide(activeFragment).show(filmsFragment)
                        .commit()
                    activeFragment = filmsFragment
                    toolbar.text = getString(R.string.top_rated_movies)
                    toolbar.visibility = View.VISIBLE
                    return@OnNavigationItemSelectedListener true
                }
                R.id.favourites -> {
                    logEvent(FAVOURITES_PAGE_CLICKED)
                    fragmentManager.beginTransaction().hide(activeFragment).show(favouritesFragment)
                        .commit()
                    activeFragment = favouritesFragment
                    toolbar.text = getString(R.string.favourite_movie)
                    toolbar.visibility = View.VISIBLE
                    return@OnNavigationItemSelectedListener true
                }
                R.id.account -> {
                    logEvent(PROFILE_PAGE_CLICKED)
                    fragmentManager.beginTransaction().hide(activeFragment).show(accountFragment)
                        .commit()
                    activeFragment = accountFragment
                    toolbar.visibility = View.GONE
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        }

}

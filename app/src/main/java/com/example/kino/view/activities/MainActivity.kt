package com.example.kino.view.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.kino.R
import com.example.kino.utils.TAG
import com.example.kino.view.fragments.AccountFragment
import com.example.kino.view.fragments.FavouritesFragment
import com.example.kino.view.fragments.FilmsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: TextView

    private val fragmentManager: FragmentManager = supportFragmentManager
    private var activeFragment: Fragment = FilmsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener(navListener)

        fragmentManager.beginTransaction().add(R.id.frame, FilmsFragment(), TAG).commit()
    }

    private val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.films -> {
                    activeFragment = FilmsFragment()
                    fragmentManager.beginTransaction().replace(R.id.frame, activeFragment).commit()
                    toolbar.text = getString(R.string.top_rated_movies)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.favourites -> {
                    activeFragment = FavouritesFragment()
                    fragmentManager.beginTransaction().replace(R.id.frame, activeFragment).commit()
                    toolbar.text = getString(R.string.favourite_movie)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.account -> {
                    activeFragment = AccountFragment()
                    fragmentManager.beginTransaction().replace(R.id.frame, activeFragment).commit()
                    toolbar.visibility = View.GONE
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        }

}

package com.example.kino.Activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.kino.Fragments.AccountFragment
import com.example.kino.Fragments.FavouritesFragment
import com.example.kino.Fragments.FilmsFragment
import com.example.kino.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity()  {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var toolbar: TextView

    private val fm: FragmentManager = supportFragmentManager
    private var activeFragment: Fragment = FilmsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)

        sharedPref = getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)

        fm.beginTransaction().add(R.id.frame,FilmsFragment(), "1").commit()
    }

    private val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.films -> {
                    activeFragment=FilmsFragment()
                    fm.beginTransaction().replace(R.id.frame,activeFragment).commit()
                    toolbar.text = "Top-rated movies"
                    return@OnNavigationItemSelectedListener true
                }
                R.id.favourites -> {
                    activeFragment=FavouritesFragment()
                    fm.beginTransaction().replace(R.id.frame,activeFragment).commit()
                    toolbar.text = "Favourite movies"
                    return@OnNavigationItemSelectedListener true
                }
                R.id.account -> {
                    activeFragment=AccountFragment()
                    fm.beginTransaction().replace(R.id.frame,activeFragment).commit()
                    toolbar.visibility= View.GONE
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        }

}

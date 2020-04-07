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

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var toolbar: TextView

    private val fragmentManager: FragmentManager = supportFragmentManager
    private var activeFragment: Fragment = FilmsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)

        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)


        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener(navListener)

        fragmentManager.beginTransaction().add(R.id.frame,FilmsFragment(), "1").commit()
    }

    private val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.films -> {
                    activeFragment=FilmsFragment()
                    fragmentManager.beginTransaction().replace(R.id.frame,activeFragment).commit()
                    toolbar.text = "Top-rated movies"
                    return@OnNavigationItemSelectedListener true
                }
                R.id.favourites -> {
                    activeFragment=FavouritesFragment()
                    fragmentManager.beginTransaction().replace(R.id.frame,activeFragment).commit()
                    toolbar.text = "Favourite movies"
                    return@OnNavigationItemSelectedListener true
                }
                R.id.account -> {
                    activeFragment=AccountFragment()
                    fragmentManager.beginTransaction().replace(R.id.frame,activeFragment).commit()
                    toolbar.visibility= View.GONE
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        }

}

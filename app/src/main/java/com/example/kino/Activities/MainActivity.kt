package com.example.kino.Activities

import android.content.Context
import android.content.Intent
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

    lateinit var sharedPref: SharedPreferences

    lateinit var toolbar: TextView

    private val fragment1: Fragment = FilmsFragment()
    private val fragment2: Fragment = FavouritesFragment()
    private val fragment3: Fragment = AccountFragment()

    private val fm: FragmentManager = supportFragmentManager
    private var active: Fragment = fragment1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)

        sharedPref = getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)

        //if(!sharedPref.contains(getString(R.string.session_id))){
           // val intent = Intent(this, SignInActivity::class.java)
            //startActivity(intent)
      //  }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)
        fm.beginTransaction().add(R.id.main, fragment2, "2").hide(fragment2).commit()
        fm.beginTransaction().add(R.id.main,fragment1, "1").commit()
        fm.beginTransaction().add(R.id.main, fragment3, "3").hide(fragment3).commit()
    }

    private val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.films -> {
                     fm.beginTransaction().hide(active).show(fragment1).commit()
                    active = fragment1
                    toolbar.text = "Top-rated movies"
                    return@OnNavigationItemSelectedListener true
                }
                R.id.favourites -> {
                    fm.beginTransaction().hide(active).show(fragment2).commit()
                    active = fragment2
                    toolbar.text = "Favourite movies"
                    return@OnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    fm.beginTransaction().hide(active).show(fragment3).commit()
                    active = fragment3
                    toolbar.visibility= View.GONE
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        }
}

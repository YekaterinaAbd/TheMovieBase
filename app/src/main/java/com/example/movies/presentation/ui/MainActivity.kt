package com.example.movies.presentation.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.movies.R
import com.example.movies.presentation.ui.account.AccountFragment
import com.example.movies.presentation.ui.lists.movies.MainListsFragment
import com.example.movies.presentation.ui.lists.search.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private val fragmentManager: FragmentManager = supportFragmentManager

    private val mainListsFragment: Fragment = MainListsFragment()
    private var searchFragment: Fragment = SearchFragment()
    private var accountFragment: Fragment = AccountFragment()

    private var activeFragment: Fragment = mainListsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppThemeLight)
        setContentView(R.layout.activity_main)

        createFragments()
        bindViews()
    }

    private fun bindViews() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener(navListener)
    }

    private fun createFragments() {
        fragmentManager.beginTransaction().add(R.id.frame, mainListsFragment)
            .commit()
        fragmentManager.beginTransaction().add(R.id.frame, accountFragment).hide(accountFragment)
            .commit()
        fragmentManager.beginTransaction().add(R.id.frame, searchFragment).hide(searchFragment)
            .commit()
    }

    private val navListener = NavigationBarView.OnItemSelectedListener { item ->
        with(fragmentManager) {
            when (Page.valueOf(item.itemId)) {
                Page.Main -> {
                    replaceFragments(activeFragment, mainListsFragment)
                    activeFragment = mainListsFragment
                    return@OnItemSelectedListener true
                }
                Page.Search -> {
                    replaceFragments(activeFragment, searchFragment)
                    activeFragment = searchFragment
                    return@OnItemSelectedListener true
                }
                Page.Account -> {
                    replaceFragments(activeFragment, accountFragment)
                    activeFragment = accountFragment
                    return@OnItemSelectedListener true
                }
            }
        }
    }

    enum class Page(@IdRes val id: Int) {

        Main(R.id.films),
        Search(R.id.search),
        Account(R.id.account);

        companion object {
            fun valueOf(@IdRes id: Int) = values().first { it.id == id }
        }

    }

    private fun FragmentManager.replaceFragments(currentFragment: Fragment, newFragment: Fragment) {
        this.beginTransaction().hide(currentFragment).show(newFragment).commit()
    }
}

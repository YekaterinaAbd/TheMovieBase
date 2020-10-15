package com.example.kino.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.kino.R
import com.example.kino.presentation.ThemeViewModel
import com.example.kino.presentation.ui.account.AccountFragment
import com.example.kino.presentation.ui.lists.favouries.FavouritesFragment
import com.example.kino.presentation.ui.lists.top.TopFilmsFragment
import com.example.kino.presentation.ui.movie_details.MovieDetailsFragment
import com.example.kino.presentation.ui.lists.search.SearchFragment
import com.example.kino.presentation.utils.constants.FAVOURITES_PAGE_CLICKED
import com.example.kino.presentation.utils.constants.MAIN_PAGE_CLICKED
import com.example.kino.presentation.utils.constants.PROFILE_PAGE_CLICKED
import com.example.kino.presentation.utils.constants.SEARCH_PAGE_CLICKED
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: TextView
    private lateinit var themeModeImage: ImageView
    private var themeModeState: Boolean = true //false == DarkTheme, true = LightTheme
    private var themeMode: Int = 0

    private val fragmentManager: FragmentManager = supportFragmentManager
    private var activeFragment: Fragment = TopFilmsFragment()
    private var filmsFragment: Fragment = TopFilmsFragment()
    private var favouritesFragment: Fragment = FavouritesFragment()
    private var searchFragment: Fragment = SearchFragment()
    private var accountFragment: Fragment = AccountFragment()
    private var movieDetailsFragment: Fragment = MovieDetailsFragment()

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val themeViewModel: ThemeViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        themeViewModel.getTheme()
        themeViewModel.themeStateLiveData.value?.let { themeModeState = it }
        themeMode = if (!themeModeState) {
            R.style.AppThemeDark
        } else {
            R.style.AppThemeLight
        }
        super.setTheme(themeMode)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        createFragments()
        bindViews()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        toolbar.visibility = View.VISIBLE
        bottomNavigation.visibility = View.VISIBLE
        themeModeImage.visibility = View.VISIBLE
    }

    private fun bindViews() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener(navListener)

        toolbar = findViewById(R.id.toolbar)
        themeModeImage = findViewById(R.id.themeModeImage)
        themeModeImage.setOnClickListener {
            if (themeMode == R.style.AppThemeLight) {
                themeMode = R.style.AppThemeDark
                themeModeState = false
            } else {
                themeMode = R.style.AppThemeLight
                themeModeState = true
            }
            themeViewModel.setThemeState(themeModeState)
            updateActivity()
        }
    }

    private fun updateActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    private fun logEvent(logMessage: String) {
        val bundle = Bundle()
        firebaseAnalytics.logEvent(logMessage, bundle)
    }

    private fun createFragments() {
        fragmentManager.beginTransaction().add(R.id.frame, filmsFragment).commit()
        fragmentManager.beginTransaction().add(R.id.frame, favouritesFragment)
            .hide(favouritesFragment).commit()
        fragmentManager.beginTransaction().add(R.id.frame, accountFragment).hide(accountFragment)
            .commit()
        fragmentManager.beginTransaction().add(R.id.frame, searchFragment)
            .hide(searchFragment).commit()
        fragmentManager.beginTransaction().add(R.id.frame, movieDetailsFragment)
            .hide(movieDetailsFragment)
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
                    themeModeImage.visibility = View.VISIBLE
                    return@OnNavigationItemSelectedListener true
                }
                R.id.favourites -> {
                    logEvent(FAVOURITES_PAGE_CLICKED)
                    fragmentManager.beginTransaction().hide(activeFragment).show(favouritesFragment)
                        .commit()
                    activeFragment = favouritesFragment
                    toolbar.text = getString(R.string.favourite_movie)
                    toolbar.visibility = View.VISIBLE
                    themeModeImage.visibility = View.VISIBLE
                    return@OnNavigationItemSelectedListener true
                }
                R.id.search -> {
                    logEvent(SEARCH_PAGE_CLICKED)
                    fragmentManager.beginTransaction().hide(activeFragment).show(searchFragment)
                        .commit()
                    activeFragment = searchFragment
                    toolbar.text = getString(R.string.search)
                    toolbar.visibility = View.VISIBLE
                    themeModeImage.visibility = View.VISIBLE
                    return@OnNavigationItemSelectedListener true
                }
                R.id.account -> {
                    logEvent(PROFILE_PAGE_CLICKED)
                    fragmentManager.beginTransaction().hide(activeFragment).show(accountFragment)
                        .commit()
                    activeFragment = accountFragment
                    toolbar.visibility = View.GONE
                    themeModeImage.visibility = View.GONE
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        }

}






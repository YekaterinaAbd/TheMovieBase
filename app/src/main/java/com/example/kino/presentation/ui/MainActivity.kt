package com.example.kino.presentation.ui

//import com.example.kino.presentation.ThemeViewModel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.kino.R
import com.example.kino.presentation.ui.account.AccountFragment
import com.example.kino.presentation.ui.lists.search.SearchFragment
import com.example.kino.presentation.ui.lists.top.MainListsFragment
import com.example.kino.presentation.utils.constants.MAIN_PAGE_CLICKED
import com.example.kino.presentation.utils.constants.PROFILE_PAGE_CLICKED
import com.example.kino.presentation.utils.constants.SEARCH_PAGE_CLICKED
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics


class MainActivity : AppCompatActivity() {

    // private lateinit var themeModeImage: ImageView
    private var themeModeState: Boolean = true //false == DarkTheme, true = LightTheme
    private var themeMode: Int = 0

    private val fragmentManager: FragmentManager = supportFragmentManager
    private var activeFragment: Fragment = MainListsFragment()
    private val mainListsFragment: Fragment = MainListsFragment()
    private var searchFragment: Fragment = SearchFragment()
    private var accountFragment: Fragment = AccountFragment()

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    // private val themeViewModel: ThemeViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
//        themeViewModel.getTheme()
//        themeViewModel.themeStateLiveData.value?.let { themeModeState = it }
//        themeMode = if (!themeModeState) {
//            R.style.AppThemeDark
//        } else {
//            R.style.AppThemeLight
//        }
//        super.setTheme(themeMode)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        createFragments()
        bindViews()
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        toolbar.visibility = View.VISIBLE
//        bottomNavigation.visibility = View.VISIBLE
//        //themeModeImage.visibility = View.VISIBLE
//    }

    private fun bindViews() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener(navListener)

        //toolbar = findViewById(R.id.toolbar)
        //themeModeImage = findViewById(R.id.themeModeImage)
//        themeModeImage.setOnClickListener {
//            if (themeMode == R.style.AppThemeLight) {
//                themeMode = R.style.AppThemeDark
//                themeModeState = false
//            } else {
//                themeMode = R.style.AppThemeLight
//                themeModeState = true
//            }
//            themeViewModel.setThemeState(themeModeState)
//            updateActivity()
//        }
    }

//    private fun updateActivity() {
//        val intent = intent
//        finish()
//        startActivity(intent)
//    }

    private fun logEvent(logMessage: String) {
        val bundle = Bundle()
        firebaseAnalytics.logEvent(logMessage, bundle)
    }

    private fun createFragments() {
        fragmentManager.beginTransaction().add(R.id.frame, mainListsFragment).commit()
        fragmentManager.beginTransaction().add(R.id.frame, accountFragment).hide(accountFragment)
            .commit()
        fragmentManager.beginTransaction().add(R.id.frame, searchFragment)
            .hide(searchFragment).commit()
    }

    private val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.films -> {
                    logEvent(MAIN_PAGE_CLICKED)
                    fragmentManager.beginTransaction().hide(activeFragment).show(mainListsFragment)
                        .commit()
                    activeFragment = mainListsFragment
                    return@OnNavigationItemSelectedListener true
                }
                R.id.search -> {
                    logEvent(SEARCH_PAGE_CLICKED)
                    fragmentManager.beginTransaction().hide(activeFragment).show(searchFragment)
                        .commit()
                    activeFragment = searchFragment
                    return@OnNavigationItemSelectedListener true
                }
                R.id.account -> {
                    logEvent(PROFILE_PAGE_CLICKED)
                    fragmentManager.beginTransaction().hide(activeFragment).show(accountFragment)
                        .commit()
                    activeFragment = accountFragment
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        }
}

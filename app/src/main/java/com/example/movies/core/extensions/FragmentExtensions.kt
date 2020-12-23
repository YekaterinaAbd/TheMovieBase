package com.example.movies.core.extensions

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.example.movies.R
import com.example.movies.core.NavigationAnimation

fun Fragment.showToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}

inline fun <reified T : Fragment> FragmentManager.replaceFragments(
    container: Int,
    hideFragment: Fragment? = null,
    bundle: Bundle? = null,
    animation: NavigationAnimation? = NavigationAnimation.NONE
) {
    val clazz = T::class.java
    val fragment = findFragmentByTag(clazz.name) ?: (clazz.getConstructor().newInstance().apply {
        bundle?.let {
            it.classLoader = javaClass.classLoader
            arguments = bundle
        }
    })
    commit {
        setTransactionAnimation(animation ?: NavigationAnimation.NONE)
        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        beginTransaction()
        add(container, fragment)
        addToBackStack(hideFragment?.tag)
        if (hideFragment != null) hide(hideFragment)
    }
}

fun FragmentTransaction.setTransactionAnimation(animation: NavigationAnimation) {
    when (animation) {
        NavigationAnimation.NONE -> setTransition(FragmentTransaction.TRANSIT_NONE)
        NavigationAnimation.CENTER -> setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        NavigationAnimation.SLIDE_LEFT -> {
            setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
        }
        NavigationAnimation.SLIDE_RIGHT -> {
            setCustomAnimations(
                R.anim.enter_from_left,
                R.anim.stay,
                R.anim.stay,
                R.anim.exit_to_left
            )
        }
    }
}

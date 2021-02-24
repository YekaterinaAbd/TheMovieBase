package com.example.movies.core.extensions

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.movies.R
import com.example.movies.core.NavigationAnimation

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

inline fun <reified T : Fragment> Fragment.changeFragment(
    container: Int,
    bundle: Bundle? = null,
    animation: NavigationAnimation? = NavigationAnimation.NONE
) {
    val fragmentManager: FragmentManager = parentFragmentManager
    val fragment = fragmentManager.getFragmentByClass<T>(bundle)

    fragmentManager.beginTransaction().apply {
        setTransactionAnimation(animation ?: NavigationAnimation.NONE)
        add(container, fragment)
        addToBackStack(null)
        hide(this@changeFragment)
        commit()
    }
}

inline fun <reified T : Fragment> FragmentManager.getFragmentByClass(bundle: Bundle?): Fragment {
    val clazz = T::class.java
    return findFragmentByTag(clazz.name) ?: (clazz.getConstructor().newInstance().apply {
        bundle?.let {
            it.classLoader = javaClass.classLoader
            arguments = bundle
        }
    })
}

fun FragmentTransaction.setTransactionAnimation(animation: NavigationAnimation) {
    when (animation) {
        NavigationAnimation.NONE -> setTransition(FragmentTransaction.TRANSIT_NONE)
        NavigationAnimation.CENTER -> setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        NavigationAnimation.SLIDE_LEFT -> {
            setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.stay,
                R.anim.stay,
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
        NavigationAnimation.BOTTOM -> {
            setCustomAnimations(
                R.anim.enter_from_bottom,
                R.anim.stay,
                R.anim.stay,
                R.anim.exit_to_bottom
            )
        }
    }
}

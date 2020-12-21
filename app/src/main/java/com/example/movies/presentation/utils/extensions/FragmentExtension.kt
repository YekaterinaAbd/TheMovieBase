package com.example.movies.presentation.utils.extensions

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit

fun Fragment.showToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}

inline fun <reified T : Fragment> FragmentManager.replaceFragments(
    container: Int, hideTag: Fragment? = null, bundle: Bundle? = null
) {
    val clazz = T::class.java
    val fragment = findFragmentByTag(clazz.name) ?: (clazz.getConstructor().newInstance().apply {
        bundle?.let {
            it.classLoader = javaClass.classLoader
            arguments = bundle
        }
    })
    commit {
        //setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out)
        beginTransaction()
        add(container, fragment)
        addToBackStack(null)
        if (hideTag != null) hide(hideTag)
    }
}

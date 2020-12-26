package com.example.movies.core.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent

inline fun <reified T : Activity> Context.navigateTo(vararg args: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java)
    for ((key, value) in args) {
        when (value) {
            is String -> intent.putExtra(key, value)
            is Int -> intent.putExtra(key, value)
        }
    }
    startActivity(intent)
}

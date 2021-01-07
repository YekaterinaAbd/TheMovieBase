package com.example.movies.core.extensions

import android.content.SharedPreferences
//тут!!
fun SharedPreferences.get(key: String, default: String): String {
    return if (this.contains(key)) this.getString(key, default) ?: default
    else default
}

fun SharedPreferences.put(vararg pairs: Pair<String, String>) {
    edit().apply {
        for (pair in pairs) {
            putString(pair.first, pair.second)
        }
        apply()
    }
}

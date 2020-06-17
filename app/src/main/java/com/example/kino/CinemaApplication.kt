package com.example.kino

import android.app.Application
import com.example.kino.utils.AppContainer

class CinemaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContainer.init(applicationContext)
    }
}

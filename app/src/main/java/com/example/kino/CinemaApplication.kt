package com.example.kino

import android.app.Application

class CinemaApplication : Application() {
    companion object {
        lateinit var appContainer: AppContainer
    }
    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
    }
}
package com.example.kino

import android.app.Application
import com.example.kino.utils.AppContainer

class CinemaApplication: Application() {
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
    }
}
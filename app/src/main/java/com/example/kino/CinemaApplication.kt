package com.example.kino

import android.app.Application
import com.example.kino.utils.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CinemaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CinemaApplication)
            modules(appModule)
        }
    }
}

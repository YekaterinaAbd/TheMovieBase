package com.example.kino

import android.app.Application
import com.example.kino.data.module.mapperModule
import com.example.kino.data.module.networkModule
import com.example.kino.data.module.repositoryModule
import com.example.kino.data.module.storageModule
import com.example.kino.domain.module.useCaseModule
import com.example.kino.presentation.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CinemaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CinemaApplication)
            modules(
                networkModule,
                storageModule,
                repositoryModule,
                useCaseModule,
                viewModelModule,
                mapperModule
            )
        }
    }
}

package com.example.movies

import android.app.Application
import com.example.movies.data.module.mapperModule
import com.example.movies.data.module.networkModule
import com.example.movies.data.module.repositoryModule
import com.example.movies.data.module.storageModule
import com.example.movies.domain.module.useCaseModule
import com.example.movies.presentation.module.viewModelModule
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

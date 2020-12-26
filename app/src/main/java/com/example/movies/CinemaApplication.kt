package com.example.movies

import android.app.Application
import com.example.movies.data.di.mapperModule
import com.example.movies.data.di.networkModule
import com.example.movies.data.di.repositoryModule
import com.example.movies.data.di.storageModule
import com.example.movies.domain.di.useCaseModule
import com.example.movies.presentation.di.viewModelModule
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

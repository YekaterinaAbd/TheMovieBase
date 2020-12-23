package com.example.movies.core.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel() {

    private val job = Job()
    val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    protected val uiScope = CoroutineScope(coroutineContext)

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}

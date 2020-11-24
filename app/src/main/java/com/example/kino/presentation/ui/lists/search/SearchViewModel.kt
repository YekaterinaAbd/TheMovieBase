package com.example.kino.presentation.ui.lists.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kino.data.model.entities.SearchQuery
import com.example.kino.domain.use_case.SearchUseCase
import com.example.kino.presentation.BaseViewModel
import kotlinx.coroutines.launch

class SearchViewModel(
    private val context: Context,
    private val searchUseCase: SearchUseCase
) : BaseViewModel() {

    private val _historyLiveData = MutableLiveData<HistoryState>()
    val historyLiveData: LiveData<HistoryState> = _historyLiveData

    fun getAllQueries() {
        launch {
            val queries = searchUseCase.getAllQueries()
            _historyLiveData.value = HistoryState.Result(queries)
        }
    }

    fun getLastQueries() {
        launch {
            val queries = searchUseCase.getLastQueries()
            Log.d("test22", queries.toString())
            _historyLiveData.value = HistoryState.Result(queries)
        }
    }

    fun insertQuery(query: String) {
        launch {
            val searchQuery = SearchQuery(query = query)
            val response = searchUseCase.insertQuery(searchQuery)
            if (response == null) _historyLiveData.value =
                HistoryState.Result(searchUseCase.getLastQueries())
            else _historyLiveData.value = HistoryState.Error(response)
        }
    }

    fun deleteQuery(id: Int?) {
        launch {
            if (id != null) {
                val response = searchUseCase.deleteQuery(id)
                if (response == null) _historyLiveData.value =
                    HistoryState.Result(searchUseCase.getLastQueries())
                else _historyLiveData.value = HistoryState.Error(response)
            }
        }
    }

    fun deleteAllQueries() {
        launch {
            val response = searchUseCase.deleteAllQueries()
            if (response == null) _historyLiveData.value = HistoryState.Deleted
            else _historyLiveData.value = HistoryState.Error(response)
        }
    }

    sealed class HistoryState {
        data class Result(val queries: List<SearchQuery>?) : HistoryState()
        data class Error(val error: String?) : HistoryState()
        object Deleted : HistoryState()
    }
}
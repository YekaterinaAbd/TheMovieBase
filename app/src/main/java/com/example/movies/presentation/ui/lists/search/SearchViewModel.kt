package com.example.movies.presentation.ui.lists.search

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.movies.core.base.BaseViewModel
import com.example.movies.data.model.entities.SearchQuery
import com.example.movies.domain.use_case.SearchUseCase
import kotlinx.coroutines.launch

class SearchViewModel(
    private val context: Context,
    private val searchUseCase: SearchUseCase
) : BaseViewModel() {

    private val _historyLiveData = MutableLiveData<HistoryState>()
    val historyLiveData: LiveData<HistoryState> = _historyLiveData

    fun getAllQueries() {
        uiScope.launch {
            val queries = searchUseCase.getAllQueries()
            _historyLiveData.value = HistoryState.Result(queries)
        }
    }

    fun getLastQueries() {
        uiScope.launch {
            val queries = searchUseCase.getLastQueries()
            _historyLiveData.value = HistoryState.Result(queries)
        }
    }

    fun insertQuery(query: String) {
        uiScope.launch {
            val searchQuery = SearchQuery(query = query)
            val response = searchUseCase.insertQuery(searchQuery)
            if (response == null) _historyLiveData.value =
                HistoryState.Result(searchUseCase.getLastQueries())
            else _historyLiveData.value = HistoryState.Error(response)
        }
    }

    fun deleteQuery(id: Int?) {
        uiScope.launch {
            if (id != null) {
                val response = searchUseCase.deleteQuery(id)
                if (response == null) _historyLiveData.value =
                    HistoryState.Result(searchUseCase.getLastQueries())
                else _historyLiveData.value = HistoryState.Error(response)
            }
        }
    }

    fun deleteAllQueries() {
        uiScope.launch {
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
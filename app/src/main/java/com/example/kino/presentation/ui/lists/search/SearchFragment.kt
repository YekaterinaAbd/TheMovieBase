package com.example.kino.presentation.ui.lists.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kino.R
import com.example.kino.data.model.entities.SearchQuery
import com.example.kino.domain.model.Movie
import com.example.kino.presentation.ui.MovieState
import com.example.kino.presentation.ui.lists.MoviesListViewModel
import com.example.kino.presentation.ui.movie_details.MovieDetailsFragment
import com.example.kino.presentation.utils.constants.INTENT_KEY
import com.google.android.material.appbar.AppBarLayout
import org.koin.android.ext.android.inject

class SearchFragment : Fragment() {

    private lateinit var search: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var appbar: AppBarLayout
    private var query: String? = null

    private val searchViewModel: SearchViewModel by inject()
    private val moviesViewModel: MoviesListViewModel by inject()

    private val itemClickListener = object : PaginationAdapter.ItemClickListener {
        override fun itemClick(position: Int, item: Movie) {
            val bundle = Bundle()
            bundle.putInt(INTENT_KEY, item.id)

            val movieDetailedFragment = MovieDetailsFragment()
            movieDetailedFragment.arguments = bundle

            parentFragmentManager.beginTransaction().add(R.id.framenav, movieDetailedFragment)
                .addToBackStack(null).commit()
        }
    }

    private val onQueryClickListener = object : OnQueryClickListener {
        override fun onQueryClick(item: SearchQuery) {
            search.setQuery(item.query, true)
            search.isIconified = false
        }

        override fun onQueryRemove(item: SearchQuery) {
            searchViewModel.deleteQuery(item.id)
        }
    }

    private val adapter by lazy {
        PaginationAdapter(itemClickListener = itemClickListener)
    }

    private val searchHistoryAdapter by lazy {
        SearchHistoryAdapter(onQueryClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setAdapter()
        getSearchHistory()
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = findViewById(R.id.recycler_view)
        searchHistoryRecyclerView = findViewById(R.id.search_history_recycler_view)
        search = findViewById(R.id.search)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        appbar = findViewById(R.id.appbar)

        val searchCloseButtonId: Int = search.context.resources
            .getIdentifier("android:id/search_close_btn", null, null)
        val closeButton: ImageView = search.findViewById(searchCloseButtonId) as ImageView

        closeButton.setOnClickListener {
            adapter.submitList(null)
            searchHistoryRecyclerView.visibility = View.VISIBLE
            search.setQuery("", false)
            query = null
            search.clearFocus()
        }

        swipeRefreshLayout.setOnRefreshListener {
            adapter.submitList(null)
            if (!query.isNullOrEmpty())
                query?.let { searchMovies(it) }
            swipeRefreshLayout.isRefreshing = false
        }

        search.setOnClickListener {
            search.isIconified = false
            appbar.setExpanded(false, true)
        }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                //Performs search when user hit the search button on the keyboard
                if (!p0.isNullOrEmpty()) {
                    searchHistoryRecyclerView.visibility = View.GONE
                    adapter.submitList(null)
                    query = p0
                    searchMovies(query!!)
                    insertQuery(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //Start filtering the list as user start entering the characters
                return false
            }
        })
    }

    private fun setAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(context)
        searchHistoryRecyclerView.adapter = searchHistoryAdapter
    }

    private fun getSearchHistory() {
        searchViewModel.getLastQueries()
        searchViewModel.historyLiveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is SearchViewModel.HistoryState.Result -> {
                    searchHistoryAdapter.addItems(result.queries)
                }
            }
        })
    }

    private fun insertQuery(query: String?) {
        if (!query.isNullOrEmpty()) {
            searchViewModel.insertQuery(query)
        }
    }

    private fun searchMovies(query: String) {
        moviesViewModel.searchMovies(query)

        moviesViewModel.searchPagedList.observe(viewLifecycleOwner, { list ->
            adapter.submitList(list)
        })

        moviesViewModel.searchStateLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is MovieState.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MovieState.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MovieState.HidePageLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MovieState.Error -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
            adapter.setNetworkState(it)
        })
    }
}
